package org.beetl.sql.springboot.dynamicds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@Autowired
	DynamicDSService dynamicService;
	String dsName = "hellogo";
	@RequestMapping("/testCommit")
	public boolean  testCommit(){
		dynamicService.successCommit(dsName,5);
		boolean commit = dynamicService.testCommit(dsName,5);
		return commit;
	}

	@RequestMapping("/testRollback")
	public boolean testRollback(){
		try{
			dynamicService.rollback(dsName,7);
		}catch (RuntimeException ex){
			System.out.println(ex.getMessage());
		}

		boolean commit = dynamicService.testCommit(dsName,7);
		return commit;
	}

	@RequestMapping("/test")
	public boolean testDefault(){
		try{
			dynamicService.testDefault(7);
		}catch (RuntimeException ex){
			System.out.println(ex.getMessage());
		}

		boolean commit = dynamicService.testDefaultCommit(7);
		return commit;
	}

}
