package com.litepaltest.test.crud.update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.litepaltest.model.Cellphone;
import com.litepaltest.model.Classroom;
import com.litepaltest.model.IdCard;
import com.litepaltest.model.Student;
import com.litepaltest.model.Teacher;
import com.litepaltest.test.LitePalTestCase;

public class UpdateUsingSaveMethodTest extends LitePalTestCase {

	private Classroom c1;

	private Classroom c2;

	private Student s1;

	private Student s2;

	private Student s3;

	private IdCard id1;

	private Teacher t1;

	private Teacher t2;

	private void init() {
		Calendar calendar = Calendar.getInstance();
		c1 = new Classroom();
		c1.setName("Working room");
		c2 = new Classroom();
		c2.setName("Resting room");
		s1 = new Student();
		s1.setName("Parker");
		s1.setAge(18);
		s2 = new Student();
		s2.setName("Peter");
		calendar.clear();
		calendar.set(1990, 9, 16, 0, 0, 0);
		s2.setBirthday(calendar.getTime());
		s2.setAge(19);
		s3 = new Student();
		s3.setName("Miley");
		s3.setAge(16);
		id1 = new IdCard();
		id1.setNumber("999777123");
		id1.setAddress("Zhushan road");
		t1 = new Teacher();
		t1.setTeacherName("Jackson");
		t1.setTeachYears(3);
		t1.setAge(28);
		t2 = new Teacher();
		t2.setTeacherName("Rose");
		t2.setTeachYears(12);
		t2.setAge(34);
	}

	public void testUpdateBasicValues() {
		Cellphone cell = new Cellphone();
		cell.setBrand("SamSung");
		cell.setPrice(3988.12);
		cell.setInStock('Y');
		assertTrue(cell.save());
		assertTrue(isDataExists(getTableName(cell), cell.getId()));
		// reduce price, sold out.
		cell.setPrice(2899.88);
		cell.setInStock('N');
		assertTrue(cell.save());
		Cellphone updatedCell = getCellPhone(cell.getId());
		assertEquals(2899.88, updatedCell.getPrice());
		assertEquals('N', updatedCell.getInStock());
	}

	public void testUpdateM2OAssociationsOnMSide() {
		init();
		s1.setClassroom(c1);
		s2.setClassroom(c1);
		assertTrue(c1.save());
		assertTrue(c2.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		s1.setClassroom(c2);
		s2.setClassroom(c2);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(1989, 7, 7, 0, 0, 0);
		s2.setBirthday(calendar.getTime());
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertEquals(c2.get_id(), getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(c2.get_id(), getForeignKeyValue("student", "classroom", s2.getId()));
		Student student2 = DataSupport.find(Student.class, s2.getId());
		calendar.clear();
		calendar.set(1989, 7, 7, 0, 0, 0);
		assertEquals(calendar.getTimeInMillis(), student2.getBirthday().getTime());
	}

	public void testUpdateM2OAssociationsOnOSide() {
		init();
		c1.getStudentCollection().add(s1);
		c1.getStudentCollection().add(s2);
		assertTrue(c1.save());
		assertTrue(c2.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		c2.getStudentCollection().add(s1);
		c2.getStudentCollection().add(s2);
		assertTrue(c2.save());
		assertEquals(c2.get_id(), getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(c2.get_id(), getForeignKeyValue("student", "classroom", s2.getId()));
	}

	public void testUpdateM2OAssociationsOnMSideWithNotSavedModel() {
		init();
		s1.setClassroom(c1);
		s2.setClassroom(c1);
		assertTrue(c1.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		s1.setClassroom(c2);
		s2.setClassroom(c2);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertEquals(c1.get_id(), getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(c1.get_id(), getForeignKeyValue("student", "classroom", s2.getId()));
	}

	public void testUpdateM2OAssociationsOnOSideWithNotSavedModel() {
		init();
		c1.getStudentCollection().add(s1);
		c1.getStudentCollection().add(s2);
		assertTrue(c1.save());
		assertTrue(c2.save());
		assertTrue(s1.save());
		c2.getStudentCollection().add(s1);
		c2.getStudentCollection().add(s2);
		assertTrue(c2.save());
		assertEquals(c2.get_id(), getForeignKeyValue("student", "classroom", s1.getId()));
	}

	public void testUpdateM2OAssociationsOnMSideWithNull() {
		init();
		s1.setClassroom(c1);
		s2.setClassroom(c1);
		assertTrue(c1.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		s1.setClassroom(null);
		s2.setClassroom(null);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertEquals(0, getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(0, getForeignKeyValue("student", "classroom", s2.getId()));
	}

	public void testUpdateM2OAssociationsOnOSideWithNull() {
		init();
		s1.setClassroom(c1);
		s2.setClassroom(c1);
		assertTrue(c1.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		c1.setStudentCollection(null);
		assertTrue(c1.save());
		assertEquals(0, getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(0, getForeignKeyValue("student", "classroom", s2.getId()));
	}

	public void testUpdateM2OAssociationsOnOSideWithEmptyCollection() {
		init();
		s1.setClassroom(c1);
		s2.setClassroom(c1);
		assertTrue(c1.save());
		assertTrue(s1.save());
		assertTrue(s2.save());
		c1.getStudentCollection().clear();
		assertTrue(c1.save());
		assertEquals(0, getForeignKeyValue("student", "classroom", s1.getId()));
		assertEquals(0, getForeignKeyValue("student", "classroom", s2.getId()));
	}

	public void testUpdateO2OAssociations() {
		init();
		assertTrue(s3.save());
		assertTrue(id1.save());
		s3.setIdcard(id1);
		id1.setStudent(s3);
		assertTrue(s3.save());
		assertTrue(id1.save());
		assertEquals(s3.getId(), getForeignKeyValue("idcard", "student", id1.getId()));
		assertEquals(id1.getId(), getForeignKeyValue("student", "idcard", s3.getId()));
	}

	public void testUpdateO2OAssociationsWithNull() {
		init();
		s3.setIdcard(id1);
		id1.setStudent(s3);
		assertTrue(s3.save());
		assertTrue(id1.save());
		s3.setIdcard(null);
		id1.setStudent(null);
		assertTrue(s3.save());
		assertTrue(id1.save());
		assertEquals(0, getForeignKeyValue("idcard", "student", id1.getId()));
		assertEquals(0, getForeignKeyValue("student", "idcard", s3.getId()));
	}

	public void testUpdateM2MAssociations() {
		init();
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		List<Teacher> teachers = new LinkedList<Teacher>();
		teachers.add(t1);
		teachers.add(t2);
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		List<Student> students = new ArrayList<Student>();
		students.add(s1);
		students.add(s2);
		students.add(s3);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		assertTrue(isIntermediateDataCorrect(getTableName(s1), getTableName(t1), s1.getId(),
				t1.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s2), getTableName(t1), s2.getId(),
				t1.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s3), getTableName(t1), s3.getId(),
				t1.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s1), getTableName(t2), s1.getId(),
				t2.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s2), getTableName(t2), s2.getId(),
				t2.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s3), getTableName(t2), s3.getId(),
				t2.getId()));
	}

	public void testUpdateM2MAssociationsWithNull() {
		init();
		List<Teacher> teachers = new LinkedList<Teacher>();
		teachers.add(t1);
		teachers.add(t2);
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		List<Student> students = new ArrayList<Student>();
		students.add(s1);
		students.add(s2);
		students.add(s3);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		s1.setTeachers(null);
		s2.setTeachers(null);
		s3.setTeachers(null);
		t1.setStudents(null);
		t2.setStudents(null);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		assertFalse(isIntermediateDataCorrect(getTableName(s1), getTableName(t1), s1.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s2), getTableName(t1), s2.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s3), getTableName(t1), s3.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s1), getTableName(t2), s1.getId(),
				t2.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s2), getTableName(t2), s2.getId(),
				t2.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s3), getTableName(t2), s3.getId(),
				t2.getId()));
	}

	public void testUpdateM2MAssociationsWithRefreshedCollection() {
		init();
		List<Teacher> teachers = new LinkedList<Teacher>();
		teachers.add(t1);
		teachers.add(t2);
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		List<Student> students = new ArrayList<Student>();
		students.add(s1);
		students.add(s2);
		students.add(s3);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		teachers.clear();
		teachers.add(t2);
		students.clear();
		students.add(s3);
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		assertFalse(isIntermediateDataCorrect(getTableName(s1), getTableName(t1), s1.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s2), getTableName(t1), s2.getId(),
				t1.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s3), getTableName(t1), s3.getId(),
				t1.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s1), getTableName(t2), s1.getId(),
				t2.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s2), getTableName(t2), s2.getId(),
				t2.getId()));
		assertTrue(isIntermediateDataCorrect(getTableName(s3), getTableName(t2), s3.getId(),
				t2.getId()));
	}

	public void testUpdateM2MAssociationsWithEmptyCollection() {
		init();
		List<Teacher> teachers = new LinkedList<Teacher>();
		teachers.add(t1);
		teachers.add(t2);
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		List<Student> students = new ArrayList<Student>();
		students.add(s1);
		students.add(s2);
		students.add(s3);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		teachers.clear();
		students.clear();
		s1.setTeachers(teachers);
		s2.setTeachers(teachers);
		s3.setTeachers(teachers);
		t1.setStudents(students);
		t2.setStudents(students);
		assertTrue(s1.save());
		assertTrue(s2.save());
		assertTrue(s3.save());
		assertTrue(t1.save());
		assertTrue(t2.save());
		assertFalse(isIntermediateDataCorrect(getTableName(s1), getTableName(t1), s1.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s2), getTableName(t1), s2.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s3), getTableName(t1), s3.getId(),
				t1.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s1), getTableName(t2), s1.getId(),
				t2.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s2), getTableName(t2), s2.getId(),
				t2.getId()));
		assertFalse(isIntermediateDataCorrect(getTableName(s3), getTableName(t2), s3.getId(),
				t2.getId()));
	}

}
