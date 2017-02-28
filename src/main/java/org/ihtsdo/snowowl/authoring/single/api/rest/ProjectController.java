package org.ihtsdo.snowowl.authoring.single.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.rcarz.jiraclient.JiraException;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.snowowl.authoring.single.api.pojo.*;
import org.ihtsdo.snowowl.authoring.single.api.service.TaskAttachment;
import org.ihtsdo.snowowl.authoring.single.api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("Authoring Projects")
@RestController
@RequestMapping(produces={MediaType.APPLICATION_JSON_VALUE})
public class ProjectController {

	@Autowired
	private TaskService taskService;

	@ApiOperation(value="List authoring Projects")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects", method= RequestMethod.GET)
	public List<AuthoringProject> listProjects() throws JiraException, BusinessServiceException {
		return taskService.listProjects();
	}

	@ApiOperation(value="Retrieve an authoring Project")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/{projectKey}", method= RequestMethod.GET)
	public AuthoringProject retrieveProject(@PathVariable final String projectKey) throws BusinessServiceException {
		return taskService.retrieveProject(projectKey);
	}
	
	@ApiOperation(value="Retrieve status information about the MAIN branch")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/main", method= RequestMethod.GET)
	public AuthoringMain retrieveMain() throws BusinessServiceException {
		return taskService.retrieveMain();
	}

	@ApiOperation(value="List Tasks within a Project")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/{projectKey}/tasks", method= RequestMethod.GET)
	public List<AuthoringTask> listTasks(@PathVariable final String projectKey) throws BusinessServiceException {
		return taskService.listTasks(projectKey);
	}

	@ApiOperation(value="List authenticated user's Tasks across Projects")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/my-tasks", method= RequestMethod.GET)
	public List<AuthoringTask> listMyTasks() throws JiraException, BusinessServiceException {
		return taskService.listMyTasks(ControllerHelper.getUsername());
	}

	@ApiOperation(value="List review tasks, with the current user or unassigned reviewer, across Projects")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/review-tasks", method= RequestMethod.GET)
	public List<AuthoringTask> listMyOrUnassignedReviewTasks() throws JiraException, BusinessServiceException {
		return taskService.listMyOrUnassignedReviewTasks();
	}

	@ApiOperation(value="Retrieve a Task within a Project")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/{projectKey}/tasks/{taskKey}", method= RequestMethod.GET)
	public AuthoringTask retrieveTask(@PathVariable final String projectKey, @PathVariable final String taskKey) throws BusinessServiceException {
		return taskService.retrieveTask(projectKey, taskKey);
	}

	@ApiOperation(value="Create a Task within a Project")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/projects/{projectKey}/tasks", method= RequestMethod.POST)
	public AuthoringTask createTask(@PathVariable final String projectKey, @RequestBody final AuthoringTaskCreateRequest taskCreateRequest) throws BusinessServiceException {
		return taskService.createTask(projectKey, taskCreateRequest);
	}

	@ApiOperation(value = "Update a Task")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/projects/{projectKey}/tasks/{taskKey}", method = RequestMethod.PUT)
	public AuthoringTask updateTask(@PathVariable final String projectKey, @PathVariable final String taskKey,  @RequestBody final AuthoringTaskUpdateRequest updatedTask) throws BusinessServiceException {
		return taskService.updateTask(projectKey, taskKey, updatedTask);
	}
	
	@ApiOperation(value = "Retrieve Task Attachments")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/projects/{projectKey}/tasks/{taskKey}/attachments", method = RequestMethod.GET)
	public List<TaskAttachment> getAttachmentsForTask(@PathVariable final String projectKey, @PathVariable final String taskKey) throws BusinessServiceException {
		return taskService.getTaskAttachments(projectKey, taskKey);
	}
	
	@ApiOperation(value = "Leave comment for Task")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/projects/{projectKey}/tasks/{taskKey}/comment", method = RequestMethod.POST)
	public void leaveComment(@PathVariable final String projectKey, @PathVariable final String taskKey, @RequestBody final String comment) throws BusinessServiceException {
		taskService.leaveCommentForTask(projectKey, taskKey, comment);
	}

}