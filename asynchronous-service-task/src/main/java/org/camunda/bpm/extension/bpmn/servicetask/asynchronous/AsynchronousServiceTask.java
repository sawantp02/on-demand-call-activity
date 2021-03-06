/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.bpmn.servicetask.asynchronous;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.camunda.bpm.engine.impl.pvm.delegate.SignallableActivityBehavior;
import org.camunda.bpm.engine.variable.VariableMap;

import static org.camunda.bpm.extension.bpmn.servicetask.asynchronous.CompletableFutureJava8Compatibility.delayedExecutor;

/**
 * <p>This is a simple implementation of the {@link SignallableActivityBehavior}
 * interface.</p> 
 * 
 * <p>The {@link SignallableActivityBehavior} provides two methods:
 * <ul>
 * 
 *   <li>The {@link #execute(ActivityExecution)}-Method is invoked when the service 
 *   task is entered. It is typically used for sending an asynchronous message to the
 *   actual service. When the method returns, the process engine will NOT continue
 *   execution. The {@link SignallableActivityBehavior} acts as a wait state.</li>
 *   
 *   <li>The {@link #signal(ActivityExecution, String, Object)} method is invoked as 
 *   the process engine is being triggered by the callback. The signal-Method is 
 *   responsible for leaving the service task activity.</li>
 * </ul>
 * </p>
 *    
 * <p>The asynchronous nature of the invocation decouples the process engine from
 * the service implementation. The process engine does not propagate any thread context
 * to the service implementation. Most prominently, the service implementation is not 
 * invoked in the context of the process engine transaction. On the contrary, from the 
 * point of view of the process engine, the {@link SignallableActivityBehavior} is a 
 * wait state: after the execute()-Method returns, the process engine will stop execution,
 * flush out the sate of the execution to the database and wait for the callback to 
 * occur.</p>
 * 
 * <p>If a failure occurs in the service implementation, the failure will not cause the 
 * process engine to roll back. The reason is that the service implementation is not 
 * directly invoked by the process engine and does not participate in the process 
 * engine transaction.</p>
 *  
 */
public class AsynchronousServiceTask extends AbstractBpmnActivityBehavior {

  public static final String EXECUTION_ID = "executionId";

	public void execute(final ActivityExecution execution) throws Exception {
	  
      // get variables
      VariableMap inputVariables = execution.getVariablesTyped();
      VariableMap inputVariablesLocal = execution.getVariablesLocalTyped();

	  String executionId = execution.getId();
	  
	  // Publish a task to a scheduled executor. This method returns after the task has 
	  // been put into the executor. The actual service implementation (lambda) will not yet 
	  // be invoked:
	  RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
	  // TODO prepare REST request using input variables
      CompletableFuture.runAsync(() -> { // simulates the sending of a non-blocking REST request
        // the code inside this lambda runs in a separate thread outside the TX
        // this will not work: execution.setVariable("foo", "bar");
        System.out.println("Hello");
        Map<String, Object> newVariables = new HashMap<>();
        newVariables.put("foo", "bar");
        runtimeService.signal(executionId, newVariables);
      }, delayedExecutor(250L, TimeUnit.MILLISECONDS));
	  
	}
			
	public void signal(ActivityExecution execution, String signalName, Object signalData) throws Exception {
	  
	  // leave the service task activity:
	  leave(execution);
	}

}
