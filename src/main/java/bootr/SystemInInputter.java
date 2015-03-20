package bootr;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemInInputter implements Inputter {

  private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  @Override
  public String ask(String key) throws Exception {
    System.out.print(key + ": ");
    return br.readLine();
  }

}
