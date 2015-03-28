package bootr;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;

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
}
