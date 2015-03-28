package bootr;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemInInputter implements Inputter {

  private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  @Override
  public String ask(String key, String detail) throws Exception {
    System.out.println(detail);
    System.out.print(key + ": ");
    return br.readLine();
  }

}
