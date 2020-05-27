package com.zoportfolio.tasklistproject;

import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTaskTest {

    @Test
    public void testUserTaskConstructor() {

        //Test that this user task has the expected name/notification time, a false task checked, and a null description.
        UserTask userTask1 = new UserTask("name", "9/99/9");

        assertEquals("name", userTask1.getTaskName());
        assertEquals("9/99/9", userTask1.getTaskNotificationTime());
        assertFalse(userTask1.getTaskChecked());
        assertNull(userTask1.getTaskDescription());

        //Test that this user task has the expected name/notification time/description, and a true task checked.
        UserTask userTask2 = new UserTask("Code", "8/59/am", "code daily", true);

        assertEquals("Code", userTask2.getTaskName());
        assertEquals("8/59/am", userTask2.getTaskNotificationTime());
        assertEquals("code daily", userTask2.getTaskDescription());
        assertTrue(userTask2.getTaskChecked());
    }

    @Test
    public void testUserTaskJSON() {

        //Test that the user task can turn into JSON succesfully.
        UserTask userTask1 = new UserTask("name", "9/99/9");
        String userTaskJSON = userTask1.toJSONString();

        //TODO: Have to mock the JSON functionality.
        assertEquals(userTaskJSON, userTask1.toJSONString());

    }

}
