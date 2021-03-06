package com.senla.rakickaya.courseplanner.ui.actions.students;

import java.util.List;

import com.senla.rakickaya.courseplanner.api.beans.IStudent;
import com.senla.rakickaya.courseplanner.api.data_exchange.IRequest;
import com.senla.rakickaya.courseplanner.api.data_exchange.IResponse;
import com.senla.rakickaya.courseplanner.api.data_exchange.enums.TagsRequest;
import com.senla.rakickaya.courseplanner.api.data_exchange.enums.TagsResponse;
import com.senla.rakickaya.courseplanner.api.facade.IFacade;
import com.senla.rakickaya.courseplanner.dataExchange.RequestBuilder;
import com.senla.rakickaya.courseplanner.dependency.ServiceDI;
import com.senla.rakickaya.courseplanner.ui.api.actions.IAction;
import com.senla.rakickaya.courseplanner.ui.util.input.Input;
import com.senla.rakickaya.courseplanner.ui.util.printer.Printer;

public class StudentRemoval implements IAction {
	private ServiceDI service = ServiceDI.getInstance();
	private IFacade facade = (IFacade) service.getObject(IFacade.class);

	@Override
	public void execute() {
		IResponse response = facade.getAllStudents();
		@SuppressWarnings("unchecked")
		List<IStudent> students = (List<IStudent>) response.getObject(TagsResponse.DATA);
		Printer.showList(students);
		Input input = Input.getInstance();
		Printer.show("Input the number to remove the Course");
		int n = input.getInt();
		IRequest request = new RequestBuilder()
				.setHead(TagsRequest.ID_STUDENT, String.valueOf(students.get(n - 1).getId())).build();
		IResponse studentResponse = facade.removeStudent(request);
		Printer.show(studentResponse.getObject(TagsResponse.MESSAGE).toString());

	}

}
