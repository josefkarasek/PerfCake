/*
 * -----------------------------------------------------------------------\
 * PerfCake
 *  
 * Copyright (C) 2010 - 2013 the original author or authors.
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
 * -----------------------------------------------------------------------/
 */
package org.perfcake;

import org.perfcake.scenario.Scenario;
import org.perfcake.scenario.ScenarioLoader;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScenarioExecutionTest extends TestSetup {
   private Scenario scenario;
   private ExecutorService es;

   @BeforeMethod
   public void prepareScenario() throws Exception {
      scenario = ScenarioLoader.load("test-dummy-scenario");
   }

   @Test
   public void stopScenarioExecutionTest() throws PerfCakeException {
      scenario.init();
      final long pre = System.currentTimeMillis();
      es = Executors.newSingleThreadExecutor();
      es.submit(new ScenarioExecutorStopper(scenario, 5));
      es.shutdown();
      scenario.run();
      final long post = System.currentTimeMillis();
      final long diff = post - pre;
      Assert.assertTrue(diff >= 5000 && diff <= 6000, "The elapsed time was expected between 5s and 6s, but actualy was " + (double) diff / 1000 + "s");
   }

   @Test
   public void fullScenarioExecutionTest() throws PerfCakeException {
      scenario.init();
      final long pre = System.currentTimeMillis();
      scenario.run();
      final long post = System.currentTimeMillis();
      final long diff = post - pre;
      Assert.assertTrue(diff >= 10000 && diff <= 12000, "The elapsed time was expected between 10s and 12s, but actualy was " + (double) diff / 1000 + "s");
   }

   private class ScenarioExecutorStopper implements Runnable {
      private final Scenario scenario;
      private final int delay;

      public ScenarioExecutorStopper(final Scenario scenario, final int delay) {
         this.scenario = scenario;
         this.delay = delay;
      }

      @Override
      public void run() {
         for (int i = delay; i > 0; i--) {
            System.out.println("Stopping scenario execution in " + i + "s.");
            try {
               Thread.sleep(1000);
            } catch (final InterruptedException e) {
               e.printStackTrace();
            }
         }
         System.out.println("Stopping scenario execution...");
         scenario.stop();
      }
   }
}
