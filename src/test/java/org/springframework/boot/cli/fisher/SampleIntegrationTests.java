/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.cli.fisher;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.ivy.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.cli.command.RunCommand;

/**
 * Integration tests to exercise the samples.
 * 
 * @author Dave Syer
 */
public class SampleIntegrationTests {

	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	private RunCommand command;

	private void start(final String... sample) throws Exception {
		Future<RunCommand> future = Executors.newSingleThreadExecutor().submit(
				new Callable<RunCommand>() {
					@Override
					public RunCommand call() throws Exception {
						RunCommand command = new RunCommand();
						command.run(sample);
						return command;
					}
				});
		this.command = future.get(4, TimeUnit.MINUTES);
	}

	@Before
	public void setup() {
		System.setProperty("disableSpringSnapshotRepos", "true");
	}

	@After
	public void teardown() {
		System.clearProperty("disableSpringSnapshotRepos");
	}

	@After
	public void stop() {
		if (this.command != null) {
			this.command.stop();
		}
	}

	@Test
	public void webSample() throws Exception {
		start("samples/web.groovy");
		String result = FileUtil.readEntirely(new URL("http://localhost:8080")
				.openStream());
		assertEquals("World!", result);
	}

}
