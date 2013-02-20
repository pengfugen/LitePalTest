package com.litepaltest.test.crud.delete;

import java.util.HashSet;
import java.util.Set;

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

	public void testDeleteNoSavedModelWithNoParameter() {
		Student tony = new Student();
		tony.setName("Tony");
		tony.setAge(23);
		int rowsAffected = tony.delete();
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
