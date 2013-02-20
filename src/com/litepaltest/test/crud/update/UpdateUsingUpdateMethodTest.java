package com.litepaltest.test.crud.update;

import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import android.content.ContentValues;
import android.database.Cursor;

import com.litepaltest.model.Classroom;
import com.litepaltest.model.Computer;
import com.litepaltest.model.Student;
import com.litepaltest.model.Teacher;
import com.litepaltest.test.LitePalTestCase;

public class UpdateUsingUpdateMethodTest extends LitePalTestCase {

	private Teacher teacher;

	private Student student;

	private Classroom classroom;

	@Override
	protected void setUp() throws Exception {
		init();
	}

	private void init() {
		classroom = new Classroom();
		classroom.setName("English room");
		teacher = new Teacher();
		teacher.setTeacherName("Tony");
		teacher.setTeachYears(3);
		teacher.setAge(23);
		teacher.setSex(false);
		student = new Student();
		student.setName("Jonny");
		student.setAge(13);
		student.setClassroom(classroom);
		student.getTeachers().add(teacher);
		teacher.getStudents().add(student);
		student.save();
		teacher.save();
		classroom.save();
	}

	public void testUpdateWithStaticUpdate() {
		ContentValues values = new ContentValues();
		values.put("TEACHERNAME", "Toy");
		int rowsAffected = DataSupport.update(Teacher.class, teacher.getId(), values);
		assertEquals(1, rowsAffected);
		assertEquals("Toy", getTeacher(teacher.getId()).getTeacherName());
		values.clear();
		values.put("aGe", 15);
		rowsAffected = DataSupport.update(Student.class, student.getId(), values);
		assertEquals(1, rowsAffected);
		assertEquals(15, getStudent(student.getId()).getAge());
	}

	public void testUpdateWithStaticUpdateButWrongClass() {
		ContentValues values = new ContentValues();
		values.put("TEACHERNAME", "Toy");
		try {
			DataSupport.update(Object.class, teacher.getId(), values);
		} catch (DataSupportException e) {
			assertEquals(
					"no such table: object: , while compiling: UPDATE object SET TEACHERNAME=? WHERE id = "
							+ teacher.getId(), e.getMessage());
		}
	}

	public void testUpdateWithStaticUpdateButWrongColumn() {
		ContentValues values = new ContentValues();
		values.put("TEACHERYEARS", 13);
		try {
			DataSupport.update(Teacher.class, teacher.getId(), values);
			fail("no such column: TEACHERYEARS");
		} catch (DataSupportException e) {
			assertEquals(
					"no such column: TEACHERYEARS: , while compiling: UPDATE teacher SET TEACHERYEARS=? WHERE id = "
							+ teacher.getId(), e.getMessage());
		}
	}

	public void testUpdateWithStaticUpdateButNotExistsRecord() {
		ContentValues values = new ContentValues();
		values.put("TEACHERNAME", "Toy");
		int rowsAffected = DataSupport.update(Teacher.class, 998909, values);
		assertEquals(0, rowsAffected);
	}

	public void testUpdateWithInstanceUpdate() {
		Teacher t = new Teacher();
		t.setAge(66);
		t.setTeacherName("Jobs");
		t.setTeachYears(33);
		t.setSex(false);
		int rowsAffected = t.update(teacher.getId());
		assertEquals(1, rowsAffected);
		Teacher newTeacher = getTeacher(teacher.getId());
		assertEquals("Jobs", newTeacher.getTeacherName());
		assertEquals(33, newTeacher.getTeachYears());
		assertEquals(66, newTeacher.getAge());
	}

	public void testUpdateWithDefaultValueWithInstanceUpdate() {
		Teacher t = new Teacher();
		t.setTeacherName("");
		t.setTeachYears(0);
		t.setSex(true);
		t.setAge(22);
		int affectedTeacher = t.update(teacher.getId());
		assertEquals(0, affectedTeacher);
		Teacher newTeacher = getTeacher(teacher.getId());
		assertEquals(teacher.getAge(), newTeacher.getAge());
		assertEquals(teacher.getTeacherName(), newTeacher.getTeacherName());
		assertEquals(teacher.getTeachYears(), newTeacher.getTeachYears());
		assertEquals(teacher.isSex(), newTeacher.isSex());
		Student s = new Student();
		s.setName(null);
		s.setAge(0);
		int affectedStudent = s.update(student.getId());
		assertEquals(0, affectedStudent);
		Student newStudent = getStudent(student.getId());
		assertEquals(student.getName(), newStudent.getName());
		assertEquals(student.getAge(), newStudent.getAge());
	}

	public void testUpdateToDefaultValueWithInstanceUpdate() {
		Student s = new Student();
		s.setToDefault("age");
		s.setToDefault("name");
		int affectedStudent = s.update(student.getId());
		assertEquals(1, affectedStudent);
		Student newStudent = getStudent(student.getId());
		assertEquals(null, newStudent.getName());
		assertEquals(0, newStudent.getAge());
		Teacher t = new Teacher();
		t.setAge(45);
		t.setTeachYears(5);
		t.setTeacherName("John");
		t.setToDefault("teacherName");
		t.setToDefault("age");
		int affectedTeacher = t.update(teacher.getId());
		assertEquals(1, affectedTeacher);
		Teacher newTeacher = getTeacher(teacher.getId());
		assertEquals(22, newTeacher.getAge());
		assertEquals("", newTeacher.getTeacherName());
		assertEquals(5, newTeacher.getTeachYears());
	}

	public void testUpdateToDefaultValueWithInstanceUpdateButWrongField() {
		try {
			Teacher t = new Teacher();
			t.setToDefault("name");
			t.update(t.getId());
			fail();
		} catch (DataSupportException e) {
			assertEquals(
					"The name field in com.litepaltest.model.Teacher class is necessary which does not exist.",
					e.getMessage());
		}
	}

	public void testUpdateWithInstanceUpdateWithConstructor() {
		try {
			Computer computer = new Computer("ACER", 5444);
			computer.save();
			computer.update(computer.getId());
			fail();
		} catch (DataSupportException e) {
			assertEquals("com.litepaltest.model.Computer needs a default constructor.",
					e.getMessage());
		}
	}

	public void testUpdateWithInstanceUpdateButNotExistsRecord() {
		Teacher t = new Teacher();
		t.setTeacherName("Johnny");
		int rowsAffected = t.update(189876465);
		assertEquals(0, rowsAffected);
	}

	public void testUpdateAllWithStaticUpdate() {
		Student s;
		int[] ids = new int[5];
		for (int i = 0; i < 5; i++) {
			s = new Student();
			s.setName("Dusting");
			s.setAge(i + 10);
			s.save();
			ids[i] = s.getId();
		}
		ContentValues values = new ContentValues();
		values.put("age", 24);
		int affectedRows = DataSupport.updateAll(Student.class, new String[] {
				"name = ? and age = ?", "Dusting", "13" }, values);
		assertEquals(1, affectedRows);
		Student updatedStu = getStudent(ids[3]);
		assertEquals(24, updatedStu.getAge());
		values.clear();
		values.put("name", "Dustee");
		affectedRows = DataSupport.updateAll(Student.class, new String[] { "name = ?", "Dusting" },
				values);
		assertEquals(5, affectedRows);
		List<Student> students = getStudents(ids);
		for (Student updatedStudent : students) {
			assertEquals("Dustee", updatedStudent.getName());
		}
	}

	public void testUpdateAllRowsWithStaticUpdate() {
		Cursor c = Connector.getDatabase().query("Student", null, null, null, null, null, null);
		int allRows = c.getCount();
		c.close();
		ContentValues values = new ContentValues();
		values.put("name", "Zuckerburg");
		int affectedRows = DataSupport.updateAll(Student.class, null, values);
		assertEquals(allRows, affectedRows);
		affectedRows = DataSupport.updateAll(Student.class, new String[] {}, values);
		assertEquals(allRows, affectedRows);
		affectedRows = DataSupport.updateAll(Student.class, new String[] { "" }, values);
		assertEquals(allRows, affectedRows);
		affectedRows = DataSupport.updateAll(Student.class, new String[] { null }, values);
		assertEquals(allRows, affectedRows);
		affectedRows = DataSupport.updateAll(Student.class, new String[] { "  " }, values);
		assertEquals(allRows, affectedRows);
	}

	public void testUpdateAllWithStaticUpdateButWrongConditions() {
		ContentValues values = new ContentValues();
		values.put("name", "Dustee");
		try {
			DataSupport.updateAll(Student.class, new String[] { "name = 'Dustin'", "aaa" }, values);
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			DataSupport.updateAll(Student.class, new String[] { null, null }, values);
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			DataSupport.updateAll(Student.class, new String[] { "address = ?", "HK" }, values);
			fail();
		} catch (DataSupportException e) {
			assertEquals(
					"no such column: address: , while compiling: UPDATE student SET name=? WHERE address = ?",
					e.getMessage());
		}
	}

	public void testUpdateAllWithInstanceUpdate() {
		Student s;
		int[] ids = new int[5];
		for (int i = 0; i < 5; i++) {
			s = new Student();
			s.setName("Jessica");
			s.setAge(i + 10);
			s.save();
			ids[i] = s.getId();
		}
		Student toUpdate = new Student();
		toUpdate.setAge(24);
		int affectedRows = toUpdate.updateAll(new String[] { "name = ? and age = ?", "Jessica",
				"13" });
		assertEquals(1, affectedRows);
		Student updatedStu = getStudent(ids[3]);
		assertEquals(24, updatedStu.getAge());
		toUpdate.setAge(18);
		toUpdate.setName("Jess");
		affectedRows = toUpdate.updateAll(new String[] { "name = ?", "Jessica" });
		assertEquals(5, affectedRows);
		List<Student> students = getStudents(ids);
		for (Student updatedStudent : students) {
			assertEquals("Jess", updatedStudent.getName());
			assertEquals(18, updatedStudent.getAge());
		}
	}
	
	public void testUpdateAllRowsWithInstanceUpdate() {
		Cursor c = Connector.getDatabase().query("Student", null, null, null, null, null, null);
		int allRows = c.getCount();
		c.close();
		Student student = new Student();
		student.setName("Zuckerburg");
		int affectedRows = student.updateAll(null);
		assertEquals(allRows, affectedRows);
		affectedRows = student.updateAll(new String[] {});
		assertEquals(allRows, affectedRows);
		affectedRows = student.updateAll(new String[] { "" });
		assertEquals(allRows, affectedRows);
		affectedRows = student.updateAll(new String[] { null });
		assertEquals(allRows, affectedRows);
		affectedRows = student.updateAll(new String[] { "  " });
		assertEquals(allRows, affectedRows);
	}

	public void testUpdateAllWithDefaultValueWithInstanceUpdate() {
		Teacher tea = null;
		int[] ids = new int[5];
		for (int i = 0; i < 5; i++) {
			tea = new Teacher();
			tea.setTeacherName("Rose Jackson");
			tea.setAge(50);
			tea.setTeachYears(15);
			tea.setSex(false);
			tea.save();
			ids[i] = tea.getId();
		}
		Teacher t = new Teacher();
		t.setTeacherName("");
		t.setTeachYears(0);
		t.setSex(true);
		t.setAge(22);
		int affectedTeacher = t.updateAll(new String[] { "teachername = 'Rose Jackson'" });
		assertEquals(0, affectedTeacher);
		List<Teacher> teachers = getTeachers(ids);
		for (Teacher updatedTeacher : teachers) {
			assertEquals("Rose Jackson", updatedTeacher.getTeacherName());
			assertEquals(50, updatedTeacher.getAge());
			assertEquals(15, updatedTeacher.getTeachYears());
			assertEquals(false, updatedTeacher.isSex());
		}
	}

	public void testUpdateAllToDefaultValueWithInstanceUpdate() {
		Student stu;
		int[] ids = new int[5];
		for (int i = 0; i < 5; i++) {
			stu = new Student();
			stu.setName("Michael Jackson");
			stu.setAge(18);
			stu.save();
			ids[i] = stu.getId();
		}
		Student s = new Student();
		s.setToDefault("age");
		s.setToDefault("name");
		int affectedStudent = s.updateAll(new String[] { "name = 'Michael Jackson'" });
		assertEquals(5, affectedStudent);
		List<Student> students = getStudents(ids);
		for (Student updatedStudent : students) {
			assertEquals(null, updatedStudent.getName());
			assertEquals(0, updatedStudent.getAge());
		}
	}

	public void testUpdateAllToDefaultValueWithInstanceUpdateButWrongField() {
		try {
			Teacher t = new Teacher();
			t.setToDefault("name");
			t.updateAll(null);
			fail();
		} catch (DataSupportException e) {
			assertEquals(
					"The name field in com.litepaltest.model.Teacher class is necessary which does not exist.",
					e.getMessage());
		}
	}
	
	public void testUpdateAllWithInstanceUpdateWithConstructor() {
		try {
			Computer computer = new Computer("ACER", 5444);
			computer.save();
			computer.updateAll(null);
			fail();
		} catch (DataSupportException e) {
			assertEquals("com.litepaltest.model.Computer needs a default constructor.",
					e.getMessage());
		}
	}
	
	public void testUpdateAllWithInstanceUpdateButWrongConditions() {
		Student student = new Student();
		student.setName("Dustee");
		try {
			student.updateAll(new String[] { "name = 'Dustin'", "aaa" });
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			student.updateAll(new String[] { null, null });
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			student.updateAll(new String[] { "address = ?", "HK" });
			fail();
		} catch (DataSupportException e) {
			assertEquals(
					"no such column: address: , while compiling: UPDATE student SET name=? WHERE address = ?",
					e.getMessage());
		}
	}

}
