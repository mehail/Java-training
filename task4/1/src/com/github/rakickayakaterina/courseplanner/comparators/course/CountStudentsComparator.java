package com.github.rakickayakaterina.courseplanner.comparators.course;

import java.util.Comparator;

import com.github.rakickayakaterina.courseplanner.beans.Course;
import com.github.rakickayakaterina.courseplanner.utils.ArrayWorker;

public class CountStudentsComparator implements Comparator<Course> {

	@Override
	public int compare(Course o1, Course o2) {
		if (o1 != null && o2 != null) {
			Integer count = ArrayWorker.getLenghtArray(o1.getStudents());
			return count.compareTo(ArrayWorker.getLenghtArray(o2.getStudents()));
		} else if (o1 != null && o2 == null) {
			return 1;
		} else {
			return -1;
		}

	}

}
