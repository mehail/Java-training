package com.senla.rakickaya.courseplanner.beans;

import java.util.Date;

import com.senla.rakickaya.courseplanner.api.beans.ILecture;
import com.senla.rakickaya.courseplanner.api.beans.ILesson;
import com.senla.rakickaya.courseplanner.utils.DateWorker;

public class Lesson implements ILesson {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3315388805589371881L;
	private long id;
	private ILecture mLecture;
	private Date mDate;
	private int countStudent;

	public Lesson(long id, ILecture mLecture, Date pDate, int countStudent) {
		super();
		this.id = id;
		this.mLecture = mLecture;
		this.mDate = pDate;
		this.countStudent = countStudent;
	}

	public int getCountStudent() {
		return countStudent;
	}

	public void setCountStudent(int countStudent) {
		this.countStudent = countStudent;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public ILecture getLecture() {
		return mLecture;
	}

	public void setLecture(ILecture mLecture) {
		this.mLecture = mLecture;
	}

	public Date getDate() {
		return mDate;
	}

	@Override
	public String toString() {
		return String.format("Lesson [id=%s, mLectureName=%s, mDate=%s]", id, mLecture.getName(),
				DateWorker.dateFormat.format(mDate));
	}

}
