package bootr;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileReplicator;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.FileContentInfoFilenameFactory;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs2.provider.ram.RamFileProvider;
import org.apache.commons.vfs2.provider.zip.ZipFileProvider;
import org.junit.Before;
import org.junit.Test;

public class BootrTest {

  private final DefaultFileSystemManager fs = new DefaultFileSystemManager();
  private final Map<String, String> config = new HashMap<String, String>();
  private final Inputter inputter = new Inputter() {
    public String ask(String key, String detail) throws Exception {
      return config.get(key);
    }
  };
  private FileObject localDir;
  private Bootr b;

  @Before
  public void setup() throws Exception {
    fs.setCacheStrategy(CacheStrategy.MANUAL);
    fs.setFilesCache(new DefaultFilesCache());
    fs.setReplicator(new DefaultFileReplicator());
    fs.setFileContentInfoFactory(new FileContentInfoFilenameFactory());
    fs.addMimeTypeMap("application/zip", "zip");

    fs.addProvider("zip", new ZipFileProvider());
    fs.addProvider("https", new RamFileProvider());
    fs.addProvider("ram", new RamFileProvider());
    fs.addProvider("file", new DefaultLocalFileProvider());

    localDir = fs.resolveFile("ram:/");
    localDir.createFolder();
    b = new Bootr(fs, inputter, localDir, new GithubProject("user", "project", "master"));
  }

  @Test
  public void downloadProjectZipFromGitHub() throws Exception {
    // given a basic project layout
    FileObject tmpDir = fs.resolveFile("ram:/tmp/");
    FileObjectUtils.writeToFile(tmpDir.resolveFile("project-master/rootFile.txt"), "1234");
    FileObjectUtils.writeToFile(tmpDir.resolveFile("project-master/dir1/dirFile.txt"), "1234");

    // that is upload to github
    FileObject zipFile = fs.resolveFile("https://www.github.com/user/project/archive/master.zip");
    FileObjectUtils.writeToZipFile(zipFile, tmpDir);

    // when extracted
    b.run();

    // we copied the file as is
    assertThat(FileObjectUtils.readFromFile(localDir.resolveFile("project/rootFile.txt")), is("1234"));
    assertThat(FileObjectUtils.readFromFile(localDir.resolveFile("project/dir1/dirFile.txt")), is("1234"));
  }

}
