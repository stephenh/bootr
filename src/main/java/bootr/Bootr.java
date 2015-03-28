package bootr;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.commons.vfs2.VFS;

public class Bootr {

  public static void main(String[] args) throws Exception {
    FileSystemManager fs = VFS.getManager();
    FileObject cwd = fs.toFileObject(new File("./"));
    cwd.createFolder();
    new Bootr(fs, new SystemInInputter(), cwd, new GithubProject("stephenh", "bootr-test", "master")).run();
    // new Bootr(fs, new SystemInInputter(), cwd, new GithubProject(args[0], args[1], "master")).run();
  }

  private final Properties p = new Properties();
  private final FileSystemManager fs;
  private final Inputter inputter;
  private final GithubProject gh;
  private final FileObject projectDir;

  public Bootr(FileSystemManager fs, Inputter inputter, FileObject localDir, GithubProject gh) throws Exception {
    this.fs = fs;
    this.inputter = inputter;
    this.gh = gh;
    this.projectDir = localDir.resolveFile(gh.project);
  }

  public void run() throws Exception {
    nukeExisting();
    download();
    extractConfig();
    promptForValues();
    rewritePaths();
    rewriteContents();
  }

  public void nukeExisting() throws Exception {
    projectDir.delete(new AllFileSelector());
    projectDir.createFolder();
  }

  /** Downloads a zip archive from github to the local directory. */
  public void download() throws Exception {
    FileObject remoteZip = fs.resolveFile("https://www.github.com/" + gh.user + "/" + gh.project + "/archive/" + gh.branch + ".zip");
    FileObject ramZipCopy = fs.resolveFile("ram:/local.zip");
    FileObjectUtils.copyContent(remoteZip, ramZipCopy);
    // createFileSystem against a zip file basically exposes the zipped contents
    FileObject ramZipSystem = fs.createFileSystem(ramZipCopy);
    projectDir.copyFrom(ramZipSystem.getChild(gh.project + "-" + gh.branch), new AllFileSelector());
    ramZipSystem.close();
  }

  public void extractConfig() throws Exception {
    FileObject config = projectDir.resolveFile(".bootr");
    if (!config.exists()) {
      return;
    }
    InputStream in = config.getContent().getInputStream();
    p.load(in);
    IOUtils.closeQuietly(in);
  }

  public void promptForValues() throws Exception {
    for (String name : p.stringPropertyNames()) {
      String detail = p.getProperty(name);
      String value = inputter.ask(name, detail);
      p.setProperty(name, value);
    }
  }

  public void rewritePaths() throws Exception {
    while (true) {
      boolean somethingChanged = false;
      for (FileObject file : projectDir.findFiles(new AllFileSelector())) {
        String baseName = file.getName().getBaseName();
        String replName = replace(baseName);
        if (!baseName.equals(replName)) {
          file.moveTo(file.getParent().resolveFile(replName));
          somethingChanged = true;
          continue;
        }
      }
      if (!somethingChanged) {
        break;
      }
    }
  }

  public void rewriteContents() throws Exception {
    for (FileObject file : projectDir.findFiles(new FileTypeSelector(FileType.FILE))) {
      String contents = FileObjectUtils.readFromFile(file);
      String updated = replace(contents);
      FileObjectUtils.writeToFile(file, updated);
    }
  }

  private String replace(String content) {
    for (String name : p.stringPropertyNames()) {
      String value = p.getProperty(name);
      content = content.replaceAll("__" + name + "__", value);
    }
    return content;
  }

}
