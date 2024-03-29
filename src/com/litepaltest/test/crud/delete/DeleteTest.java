package com.litepaltest.test.crud.delete;

import java.util.HashSet;
import java.util.Set;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import android.database.sqlite.SQLiteException;

import com.litepaltest.model.Classroom;
import com.litepaltest.model.IdCard;
import com.litepaltest.model.Student;
import com.litepaltest.model.Teacher;
import com.litepaltest.test.LitePalTestCase;

public class DeleteTest extends LitePalTestCase {

	private Classroom gameRoom;

	private Student jude;

	private Student rose;

	private Teacher john;

	private Teacher mike;

	private IdCard judeCard;

	private IdCard roseCard;

	private IdCard johnCard;

	private IdCard mikeCard;

	private void createClassroomStudentsTeachers() {
		initGameRoom();
		initRose();
		initJude();
		initMike();
		initJohn();
		Set<Student> students = new HashSet<Student>();
		students.add(rose);
		students.add(jude);
		gameRoom.setStudentCollection(students);
		gameRoom.getTeachers().add(john);
		gameRoom.getTeachers().add(mike);
		gameRoom.save();
		rose.save();
		jude.save();
		john.save();
		mike.save();
	}

	private void createStudentsTeachersWithIdCard() {
		initRose();
		initJude();
		initMike();
		initJohn();
		rose.save();
		jude.save();
		mike.save();
		john.save();
		roseCard.save();
		judeCard.save();
		mikeCard.save();
		johnCard.save();
	}

	private void createStudentsTeachersWithAssociations() {
		initRose();
		initJude();
		initMike();
		initJohn();
		rose.getTeachers().add(john);
		rose.getTeachers().add(mike);
		jude.getTeachers().add(mike);
		rose.save();
		jude.save();
		john.save();
		mike.save();
	}

	public void testDeleteWithNoParameter() {
		initJude();
		jude.save();
		int rowsAffected = jude.delete();
		assertEquals(1, rowsAffected);
		Student s = getStudent(jude.getId());
		assertNull(s);
	}

	public void testDeleteById() {
		initJude();
		jude.save();
		int rowsAffected = DataSupport.delete(Student.class, jude.getId());
		assertEquals(1, rowsAffected);
		Student s = getStudent(jude.getId());
		assertNull(s);
	}

	public void testDeleteNoSavedModelWithNoParameter() {
		Student tony = new Student();
		tony.setName("Tony");
		tony.setAge(23);
		int rowsAffected = tony.delete();
		assertEquals(0, rowsAffected);
	}

	public void testDeleteWithNotExistsRecordById() {
		int rowsAffected = DataSupport.delete(Student.class, 998909);
		assertEquals(0, rowsAffected);
	}

	public void testDeleteCascadeM2OAssociationsOnMSideWithNoParameter() {
		createClassroomStudentsTeachers();
		int rowsAffected = gameRoom.delete();
		assertEquals(5, rowsAffected);
		assertNull(getClassroom(gameRoom.get_id()));
		assertNull(getStudent(jude.getId()));
		assertNull(getStudent(rose.getId()));
		assertNull(getTeacher(john.getId()));
		assertNull(getTeacher(mike.getId()));
	}

	public void testDeleteCascadeM2OAssociationsOnMSideById() {
		createClassroomStudentsTeachers();
		int rowsAffected = DataSupport.delete(Classroom.class, gameRoom.get_id());
		assertEquals(5, rowsAffected);
		assertNull(getClassroom(gameRoom.get_id()));
		assertNull(getStudent(jude.getId()));
		assertNull(getStudent(rose.getId()));
		assertNull(getTeacher(john.getId()));
		assertNull(getTeacher(mike.getId()));
	}

	public void testDeleteCascadeM2OAssociationsOnOSideWithNoParameter() {
		createClassroomStudentsTeachers();
		int rowsAffected = jude.delete();
		assertEquals(1, rowsAffected);
		assertNull(getStudent(jude.getId()));
		rowsAffected = rose.delete();
		assertEquals(1, rowsAffected);
		assertNull(getStudent(rose.getId()));
		rowsAffected = john.delete();
		assertEquals(1, rowsAffected);
		assertNull(getTeacher(john.getId()));
		rowsAffected = mike.delete();
		assertEquals(1, rowsAffected);
		assertNull(getTeacher(mike.getId()));
	}

	public void testDeleteCascadeM2OAssociationsOnOSideById() {
		createClassroomStudentsTeachers();
		int rowsAffected = DataSupport.delete(Student.class, jude.getId());
		assertEquals(1, rowsAffected);
		assertNull(getStudent(jude.getId()));
		rowsAffected = DataSupport.delete(Student.class, rose.getId());
		assertEquals(1, rowsAffected);
		assertNull(getStudent(rose.getId()));
		rowsAffected = DataSupport.delete(Teacher.class, john.getId());
		assertEquals(1, rowsAffected);
		assertNull(getTeacher(john.getId()));
		rowsAffected = DataSupport.delete(Teacher.class, mike.getId());
		assertEquals(1, rowsAffected);
		assertNull(getTeacher(mike.getId()));

	}

	public void testDeleteCascadeO2OAssociationsWithNoParameter() {
		createStudentsTeachersWithIdCard();
		int affectedRows = jude.delete();
		assertEquals(2, affectedRows);
		assertNull(getStudent(jude.getId()));
		assertNull(getIdCard(judeCard.getId()));
		affectedRows = roseCard.delete();
		assertEquals(2, affectedRows);
		assertNull(getStudent(rose.getId()));
		assertNull(getIdCard(roseCard.getId()));
		affectedRows = john.delete();
		assertEquals(2, affectedRows);
		assertNull(getTeacher(john.getId()));
		assertNull(getIdCard(johnCard.getId()));
		affectedRows = mikeCard.delete();
		assertEquals(1, affectedRows);
		assertNull(getIdCard(mikeCard.getId()));
	}

	public void testDeleteCascadeO2OAssociationsById() {
		createStudentsTeachersWithIdCard();
		int affectedRows = DataSupport.delete(Student.class, jude.getId());
		assertEquals(2, affectedRows);
		assertNull(getStudent(jude.getId()));
		assertNull(getIdCard(judeCard.getId()));
		affectedRows = DataSupport.delete(IdCard.class, roseCard.getId());
		assertEquals(2, affectedRows);
		assertNull(getStudent(rose.getId()));
		assertNull(getIdCard(roseCard.getId()));
		affectedRows = DataSupport.delete(Teacher.class, john.getId());
		assertEquals(2, affectedRows);
		assertNull(getTeacher(john.getId()));
		assertNull(getIdCard(johnCard.getId()));
		affectedRows = DataSupport.delete(IdCard.class, mikeCard.getId());
		assertEquals(1, affectedRows);
		assertNull(getIdCard(mikeCard.getId()));
	}

	public void testDeleteCascadeM2MAssociationsWithNoParameter() {
		createStudentsTeachersWithAssociations();
		int rowsAffected = jude.delete();
		assertEquals(2, rowsAffected);
		assertNull(getStudent(jude.getId()));
		assertM2MFalse("student", "teacher", jude.getId(), mike.getId());
		assertM2M("student", "teacher", rose.getId(), mike.getId());
		assertM2M("student", "teacher", rose.getId(), john.getId());
		createStudentsTeachersWithAssociations();
		rowsAffected = rose.delete();
		assertEquals(3, rowsAffected);
		assertNull(getStudent(rose.getId()));
		assertM2MFalse("student", "teacher", rose.getId(), mike.getId());
		assertM2MFalse("student", "teacher", rose.getId(), john.getId());
		assertM2M("student", "teacher", jude.getId(), mike.getId());
	}

	public void testDeleteCascadeM2MAssociationsById() {
		createStudentsTeachersWithAssociations();
		int rowsAffected = DataSupport.delete(Teacher.class, john.getId());
		assertEquals(2, rowsAffected);
		assertNull(getTeacher(john.getId()));
		assertM2MFalse("student", "teacher", rose.getId(), john.getId());
		assertM2M("student", "teacher", rose.getId(), mike.getId());
		assertM2M("student", "teacher", jude.getId(), mike.getId());
		createStudentsTeachersWithAssociations();
		rowsAffected = DataSupport.delete(Teacher.class, mike.getId());
		assertEquals(3, rowsAffected);
		assertNull(getTeacher(mike.getId()));
		assertM2MFalse("student", "teacher", rose.getId(), mike.getId());
		assertM2MFalse("student", "teacher", jude.getId(), mike.getId());
		assertM2M("student", "teacher", rose.getId(), john.getId());
	}

	public void testDeleteAll() {
		Student s;
		int[] ids = new int[5];
		for (int i = 0; i < 5; i++) {
			s = new Student();
			s.setName("Dusting");
			s.setAge(i + 10);
			s.save();
			ids[i] = s.getId();
		}
		int affectedRows = DataSupport.deleteAll(Student.class, "name = ? and age = ?", "Dusting",
				"13");
		assertEquals(1, affectedRows);
		assertNull(getStudent(ids[3]));
		affectedRows = DataSupport.deleteAll(Student.class, "name = 'Dusting'");
		assertEquals(4, affectedRows);
	}

	public void testDeleteAllRows() {
		createStudentsTeachersWithIdCard();
		int rowsCount = getRowsCount("teacher");
		int affectedRows = 0;
		affectedRows = DataSupport.deleteAll(Teacher.class);
		assertEquals(rowsCount, affectedRows);
		rowsCount = getRowsCount("student");
		affectedRows = DataSupport.deleteAll(Student.class);
		assertEquals(rowsCount, affectedRows);
		rowsCount = getRowsCount("idcard");
		affectedRows = DataSupport.deleteAll(IdCard.class);
		assertEquals(rowsCount, affectedRows);
		createStudentsTeachersWithAssociations();
		rowsCount = getRowsCount("teacher");
		affectedRows = DataSupport.deleteAll(Teacher.class);
		assertEquals(rowsCount, affectedRows);
		rowsCount = getRowsCount("student");
		affectedRows = DataSupport.deleteAll(Student.class);
		assertEquals(rowsCount, affectedRows);
		rowsCount = getRowsCount("student_teacher");
		affectedRows = DataSupport.deleteAll("student_teacher");
		assertEquals(rowsCount, affectedRows);
	}

	public void testDeleteAllWithWrongConditions() {
		try {
			DataSupport.deleteAll(Student.class, "name = 'Dustin'", "aaa");
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			DataSupport.deleteAll(Student.class, null, null);
			fail();
		} catch (DataSupportException e) {
			assertEquals("The parameters in conditions are incorrect.", e.getMessage());
		}
		try {
			DataSupport.deleteAll(Student.class, "address = ?", "HK");
			fail();
		} catch (SQLiteException e) {
		}
	}

	private void initGameRoom() {
		gameRoom = new Classroom();
		gameRoom.setName("Game room");
	}

	private void initJude() {
		jude = new Student();
		jude.setName("Jude");
		jude.setAge(13);
		judeCard = new IdCard();
		judeCard.setAddress("Jude Street");
		judeCard.setNumber("123456");
		jude.setIdcard(judeCard);
		judeCard.setStudent(jude);
	}

	private void initRose() {
		rose = new Student();
		rose.setName("Rose");
		rose.setAge(15);
		roseCard = new IdCard();
		roseCard.setAddress("Rose Street");
		roseCard.setNumber("123457");
		roseCard.setStudent(rose);
	}

	private void initJohn() {
		john = new Teacher();
		john.setTeacherName("John");
		john.setAge(33);
		john.setTeachYears(13);
		johnCard = new IdCard();
		johnCard.setAddress("John Street");
		johnCard.setNumber("123458");
		john.setIdCard(johnCard);
	}

	private void initMike() {
		mike = new Teacher();
		mike.setTeacherName("Mike");
		mike.setAge(36);
		mike.setTeachYears(16);
		mikeCard = new IdCard();
		mikeCard.setAddress("Mike Street");
		mikeCard.setNumber("123459");
		mike.setIdCard(mikeCard);
	}

}
