package org.beetl.sql.saga.ms.server.web;

import org.beetl.sql.saga.common.SagaRollbackTask;
import org.beetl.sql.saga.ms.server.entity.RollbackEntity;
import org.beetl.sql.saga.ms.server.entity.RollbackTaskEntity;
import org.beetl.sql.saga.ms.server.service.SagaService;
import org.beetl.sql.saga.ms.server.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TaskController {
	@Autowired
	SagaService sagaService;

	@GetMapping("allRollbackTask")
	public JsonResult<List<RollbackTaskEntity>> allRollbackTask(){
		List<RollbackTaskEntity> list = sagaService.allSagaRollbackTask();
		return JsonResult.success(list);
	}

	@GetMapping("allRollback")
	public JsonResult<List<RollbackEntity>> allRollback(){
		List<RollbackEntity> list = sagaService.allSagaRollback();
		return JsonResult.success(list);
	}
}
