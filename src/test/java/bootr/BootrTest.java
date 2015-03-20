package bootr;


import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.ram.RamFileProvider;
import org.junit.Before;
import org.junit.Test;

public class BootrTest {

  private DefaultFileSystemManager fs = new DefaultFileSystemManager();

  @Before
  public void setup() throws Exception {
    fs.setFilesCache(new NullFilesCache());
    fs.setCacheStrategy(CacheStrategy.MANUAL);
    fs.addProvider("https", new RamFileProvider());
    
    FileObject o2 = fs.resolveFile("https://foo.github.com/bar.zip");
    OutputStream out = o2.getContent().getOutputStream();
    IOUtils.write("bar", out);
    out.close();
    
    FileObject o = fs.resolveFile("https://foo.github.com/bar.zip");
    System.out.println(o);
    System.out.println(o.getContent().getSize());
  }

  @Test
  public void downloadProjectZipFromGitHub() throws FileSystemException {
    // FileSystem urls = fs.createFileSystem("url", fs.resolveFile("ram://"));
    // System.out.println(fs.resolveFile("url://foo"));
  }

}
