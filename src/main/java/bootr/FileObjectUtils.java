package bootr;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;

public class FileObjectUtils {

  public static String readFromFile(FileObject file) throws Exception {
    InputStream in = file.getContent().getInputStream();
    String content = IOUtils.toString(in);
    in.close();
    return content;
  }

  public static void writeToFile(FileObject file, String content) throws Exception {
    OutputStream out = file.getContent().getOutputStream();
    IOUtils.write(content, out);
    out.close();
  }

  public static void copyContent(FileObject from, FileObject to) throws Exception {
    InputStream in = from.getContent().getInputStream();
    OutputStream out = to.getContent().getOutputStream();
    IOUtils.copy(in, out);
    IOUtils.closeQuietly(in);
    IOUtils.closeQuietly(out);
  }

  public static void writeToZipFile(FileObject zipFile, FileObject sourceDir) throws Exception {
    zipFile.createFile();
    ZipOutputStream zos = new ZipOutputStream(zipFile.getContent().getOutputStream());
    for (FileObject file : sourceDir.findFiles(new FileTypeSelector(FileType.FILE))) {
      String relativeName = file.getName().toString().substring(sourceDir.getName().toString().length() + 1);
      ZipEntry zipEntry = new ZipEntry(relativeName);
      InputStream in = file.getContent().getInputStream();
      zos.putNextEntry(zipEntry);
      IOUtils.copy(in, zos);
      in.close();
    }
    zos.close();
    zipFile.close();
  }
}
