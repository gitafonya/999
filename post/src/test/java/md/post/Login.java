package md.post;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Login extends Actions {
	private String url = "https://999.md/ru/";

	Actions myActions = new Actions();

	 @BeforeTest
	 public void beforeTest(){
	 myActions.navigate(url);

	 }

	 @Test
	public void test() throws IOException, InterruptedException {
		HashMap<String, String> credentials = myActions.readFile();
		 myActions.login(credentials.get("Login"),credentials.get("Password"));
		 myActions.post();
		 Thread.sleep(10000);
	}
	
	
	 @AfterTest
	 public void afterTest() {
	 myActions.quit();
	 }

}