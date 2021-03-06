package com.senla.rakickaya.courseplanner.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.senla.rakickaya.courseplanner.api.beans.ICourse;
import com.senla.rakickaya.courseplanner.api.beans.ILector;
import com.senla.rakickaya.courseplanner.api.beans.ILecture;
import com.senla.rakickaya.courseplanner.api.beans.IStudent;
import com.senla.rakickaya.courseplanner.api.repositories.ICoursesRepository;
import com.senla.rakickaya.courseplanner.api.repositories.ILectorsRepository;
import com.senla.rakickaya.courseplanner.api.repositories.IStudentsRepository;
import com.senla.rakickaya.courseplanner.api.services.ICoursesService;
import com.senla.rakickaya.courseplanner.beans.Course;
import com.senla.rakickaya.courseplanner.csv.converters.ConverterFromCsv;
import com.senla.rakickaya.courseplanner.csv.converters.ConverterToCsv;
import com.senla.rakickaya.courseplanner.csv.converters.entities.CsvResponse;
import com.senla.rakickaya.courseplanner.exception.EntityNotFoundException;
import com.senla.rakickaya.courseplanner.repositories.CoursesRepository;
import com.senla.rakickaya.courseplanner.repositories.LectorsRepository;
import com.senla.rakickaya.courseplanner.repositories.StudentsRepository;
import com.senla.rakickaya.courseplanner.utils.DateWorker;
import com.senla.rakickaya.courseplanner.utils.FileWorker;
import com.senla.rakickaya.courseplanner.utils.GeneratorId;
import com.senla.rakickaya.courseplanner.utils.ListWorker;

public class CoursesService implements ICoursesService {

	private static final Logger logger = Logger.getLogger(CoursesRepository.class.getName());

	private final ICoursesRepository mRepositoryCourses;
	private final IStudentsRepository mRepositoryStudents;
	private final ILectorsRepository mRepositoryLectors;

	public CoursesService() {
		super();
		this.mRepositoryCourses = CoursesRepository.getInstance();
		this.mRepositoryStudents = StudentsRepository.getInstance();
		this.mRepositoryLectors = LectorsRepository.getInstance();
	}

	@Override
	public void addCourse(ICourse pCourse) {
		synchronized (mRepositoryCourses) {
			mRepositoryCourses.addCourse(pCourse);
		}

	}

	@Override
	public void removeCourse(long pId) throws EntityNotFoundException {
		ICourse course;
		synchronized (mRepositoryCourses) {
			course = mRepositoryCourses.removeCourse(pId);
		}
		if (course == null)
			throw new EntityNotFoundException();
		List<IStudent> students = mRepositoryStudents.getStudents();
		synchronized (mRepositoryStudents) {
			for (int i = 0; i < students.size(); i++) {
				ListWorker.removeItemById(students.get(i).getCourses(), pId);
				mRepositoryStudents.updateStudent(students.get(i));
			}
		}
	}

	@Override
	public void updateCourse(ICourse pCourse) {
		synchronized (mRepositoryCourses) {
			mRepositoryCourses.updateCourse(pCourse);
		}

	}

	@Override
	public ICourse getCourse(long pId) {
		return mRepositoryCourses.getCourse(pId);
	}

	@Override
	public List<ICourse> getCourses() {
		return mRepositoryCourses.getCourses();
	}

	@Override
	public synchronized void addStudentToCourse(long pIdStudent, long pIdCourse) {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		IStudent student = mRepositoryStudents.getStudent(pIdStudent);
		course.getStudents().add(student);
		student.getCourses().add(course);
		mRepositoryCourses.updateCourse(course);
		mRepositoryStudents.updateStudent(student);
	}

	@Override
	public synchronized void removeStudentFromCourse(long pIdStudent, long pIdCourse) throws EntityNotFoundException {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		if (course == null) {
			throw new EntityNotFoundException();
		}
		IStudent student = ListWorker.removeItemById(course.getStudents(), pIdStudent);
		if (student == null) {
			throw new EntityNotFoundException();
		}
		mRepositoryCourses.updateCourse(course);
		mRepositoryStudents.updateStudent(student);

	}

	@Override
	public synchronized void addLectorToCourse(long pIdLector, long pIdCourse) {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		ILector lector = mRepositoryLectors.getLector(pIdLector);
		course.setLector(lector);
		mRepositoryCourses.updateCourse(course);
		mRepositoryLectors.updateLector(lector);

	}

	@Override
	public synchronized void removeLectorFromCourse(long pIdLector, long pIdCourse) throws EntityNotFoundException {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		if (course == null) {
			throw new EntityNotFoundException();
		}
		ILector lector = course.getLector();
		if (lector == null) {
			throw new EntityNotFoundException();
		}
		if (lector != null && lector.getId() == pIdLector) {
			course.setLector(null);
		}
		mRepositoryCourses.updateCourse(course);
		mRepositoryLectors.updateLector(lector);
	}

	@Override
	public void addLectureToCourse(ILecture lecture, long pIdCourse) {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		course.getLectures().add(lecture);
		synchronized (mRepositoryCourses) {
			mRepositoryCourses.updateCourse(course);
		}

	}

	@Override
	public void removeLectureFromCourse(long pIdLecture, long pIdCourse) throws EntityNotFoundException {
		ICourse course = mRepositoryCourses.getCourse(pIdCourse);
		if (course == null) {
			throw new EntityNotFoundException();
		}
		ILecture lecture = ListWorker.removeItemById(course.getLectures(), pIdLecture);
		if (lecture == null) {
			throw new EntityNotFoundException();
		}
		synchronized (mRepositoryCourses) {
			mRepositoryCourses.updateCourse(course);
		}

	}

	@Override
	public List<ICourse> getSortedList(Comparator<ICourse> mComparator) {
		List<ICourse> listCourses = mRepositoryCourses.getCourses();
		listCourses.sort(mComparator);
		return listCourses;
	}

	@Override
	public List<ICourse> getCoursesAfterDate(Date pDate, Comparator<ICourse> pComparator) {
		List<ICourse> resultList = new ArrayList<>();
		List<ICourse> courses = mRepositoryCourses.getCourses();
		for (int i = 0; i < courses.size(); i++) {
			if (DateWorker.isAfterDate(pDate, courses.get(i).getStartDate())) {
				resultList.add(courses.get(i));
			}
		}
		resultList.sort(pComparator);
		return resultList;
	}

	@Override
	public List<ICourse> getCurrentCourses(Date pCurrentDate, Comparator<ICourse> pComparator) {
		List<ICourse> resultList = new ArrayList<>();
		List<ICourse> courses = mRepositoryCourses.getCourses();
		for (int i = 0; i < courses.size(); i++) {
			if (DateWorker.isBetweenDate(pCurrentDate, courses.get(i).getStartDate(), courses.get(i).getEndDate())) {
				resultList.add(courses.get(i));
			}
		}
		resultList.sort(pComparator);
		return resultList;
	}

	@Override
	public int getTotalCountCourses() {
		return mRepositoryCourses.getCourses().size();
	}

	@Override
	public List<ICourse> getPastCourses(Date startDateSub, Date endDateSub) {
		List<ICourse> resultList = new ArrayList<>();
		List<ICourse> courses = mRepositoryCourses.getCourses();
		for (int i = 0; i < courses.size(); i++) {
			if (DateWorker.isSubInterval(courses.get(i).getStartDate(), courses.get(i).getEndDate(), startDateSub,
					endDateSub)) {
				resultList.add(courses.get(i));
			}
		}
		return resultList;
	}

	private ILecture getLectureById(long idLecture) {
		for (ILecture lecture : getAllLectures()) {
			if (lecture.getId() == idLecture) {
				return lecture;
			}
		}
		return null;
	}

	@Override
	public List<ILecture> getAllLectures() {
		return mRepositoryCourses.getAllLectures();
	}

	@Override
	public void cloneCourseById(long pId) throws CloneNotSupportedException, EntityNotFoundException {
		ICourse course = mRepositoryCourses.getCourse(pId);
		if (course == null) {
			throw new EntityNotFoundException();
		}
		ICourse cloneCourse = course.clone();
		mRepositoryCourses.addCourse(cloneCourse);

	}

	@Override
	public void exportCSV(String path) {
		FileWorker worker = new FileWorker(path);
		List<String> csvEntities = new ArrayList<>();
		List<ICourse> courses = mRepositoryCourses.getCourses();
		for (ICourse course : courses) {
			try {
				String csvString;
				csvString = ConverterToCsv.convert(course);
				csvEntities.add(csvString);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		worker.write(csvEntities);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void importCSV(String path) {
		final String LECTOR = "lector";
		final String STUDENTS = "students";
		final String LECTURES = "lectures";
		List<ICourse> courses = new ArrayList<>();
		try {
			FileWorker worker = new FileWorker(path);
			List<String> list = worker.read();
			for (String str : list) {
				CsvResponse response = ConverterFromCsv.convert(str, Course.class);
				ICourse course = (ICourse) response.getEntity();
				Map<String, Object> map = response.getRelation();
				if (map.containsKey(LECTOR)) {
					Long idLector = (Long) map.get(LECTOR);
					ILector lector = mRepositoryLectors.getLector(idLector);
					course.setLector(lector);
				}
				if (map.containsKey(STUDENTS)) {
					List<Long> idStudents = (List<Long>) map.get(STUDENTS);
					List<IStudent> students = new ArrayList<>();
					for (Long idS : idStudents) {
						IStudent student = mRepositoryStudents.getStudent(idS);
						if (student != null) {
							students.add(student);
						}
					}
					course.setStudents(students);
				}
				if (map.containsKey(LECTURES)) {
					List<Long> idLectures = (List<Long>) map.get(LECTURES);
					List<ILecture> lectures = new ArrayList<>();
					for (Long idL : idLectures) {
						ILecture lecture = getLectureById(idL);
						if (lecture != null) {
							lectures.add(lecture);
						}
					}
					course.setLectures(lectures);
				}
				courses.add(course);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());

		}
		synchronized (mRepositoryCourses) {
			for (ICourse course : courses) {
				if (!mRepositoryCourses.addCourse(course)) {
					mRepositoryCourses.updateCourse(course);
				} else {
					GeneratorId generatorId = GeneratorId.getInstance();
					long id = generatorId.getIdCourse();
					if (course.getId() > id) {
						generatorId.setIdCourse(id);
					}
				}
			}
		}
	}

}
