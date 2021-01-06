package org.beetl.sql.saga.ms.server.web;

import io.swagger.annotations.ApiOperation;
import org.beetl.sql.saga.ms.server.entity.RollbackEntity;
import org.beetl.sql.saga.ms.server.entity.RollbackTaskEntity;
import org.beetl.sql.saga.ms.server.service.SagaService;
import org.beetl.sql.saga.ms.server.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TaskController {
	@Autowired
	SagaService sagaService;

	/**
	 * 得到所有回滚任务
	 * @return
	 */
	@GetMapping("allRollbackTask")
	@ApiOperation("列表SagaServer中所有的任务")
	public JsonResult<List<RollbackTaskEntity>> allRollbackTask() {
		List<RollbackTaskEntity> list = sagaService.allSagaRollbackTask();
		return JsonResult.success(list);
	}

	/**
	 * 得到所有回滚事务
	 * @return
	 */
	@GetMapping("allRollback")
	@ApiOperation("列表SagaServer中所有的事务")
	public JsonResult<List<RollbackEntity>> allRollback() {
		List<RollbackEntity> list = sagaService.allSagaRollback();
		return JsonResult.success(list);
	}

	/**
	 * 列出所有未完成的回滚事务
	 * @return
	 */
	@GetMapping("allNotSuccessRollback")
	@ApiOperation("显示SagaServer中所有未完成的事务")
	public JsonResult<List<RollbackEntity>> allNotSuccessRollback() {
		List<RollbackEntity> list = sagaService.allNotSuccessRollback();
		return JsonResult.success(list);
	}

	/**
	 * 查询回滚事务详情
	 * @param gid
	 * @return
	 */
	@GetMapping("rollbackDetail/{gid}")
	@ApiOperation("显示事务的所有回滚任务")
	public JsonResult<List<RollbackTaskEntity>> rollbackDetail(@PathVariable String gid) {
		List<RollbackTaskEntity> list = sagaService.getSagaRollbackDetail(gid);
		return JsonResult.success(list);
	}

	/**
	 * 回滚 任务
	 * @param taskId
	 * @return
	 */
	@PostMapping("rollbackTask/{taskId}")
	@ApiOperation("执行某个回滚任务")
	public JsonResult rollbackTask(@PathVariable String taskId) {
		sagaService.rollbackTaskId(taskId);
		return JsonResult.success();
	}

	/**
	 * 事务的所有失败的任务再次执行
	 * @param gid
	 * @return
	 */
	@PostMapping("rollback/{gid}")
	@ApiOperation("事务的所有失败的任务再次执行")
	public JsonResult rollback(@PathVariable String gid) {
		sagaService.rollback(gid);
		return JsonResult.success();
	}

	/**
	 * 强制回滚事务的所有任务，即使某些任务还在执行过程
	 * @param gid
	 * @return
	 */
	@PostMapping("forceRollback/{gid}")
	@ApiOperation("强制回滚事务的所有任务，即使某些任务还在执行过程")
	public JsonResult forceRollback(@PathVariable String gid) {
		sagaService.forceRollback(gid);
		return JsonResult.success();
	}

	/**
	 * 任务已经手工处理完毕，更改任务状态为成功
	 * @param gid
	 * @return
	 */
	@PostMapping("forceSuccess/{gid}")
	@ApiOperation("手工设置回滚任务执行成功")
	public JsonResult forceSuccess(@PathVariable String gid) {
		sagaService.forceRollback(gid);
		return JsonResult.success();
	}



}
