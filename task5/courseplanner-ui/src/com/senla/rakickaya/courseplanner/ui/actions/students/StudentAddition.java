package com.senla.rakickaya.courseplanner.ui.actions.students;

import java.util.HashMap;
import java.util.Map;

import com.senla.rakickaya.courseplanner.api.data_exchange.IRequest;
import com.senla.rakickaya.courseplanner.api.data_exchange.IResponse;
import com.senla.rakickaya.courseplanner.api.data_exchange.enums.TagsRequest;
import com.senla.rakickaya.courseplanner.api.data_exchange.enums.TagsResponse;
import com.senla.rakickaya.courseplanner.api.facade.IFacade;
import com.senla.rakickaya.courseplanner.ui.api.actions.IAction;
import com.senla.rakickaya.courseplanner.ui.util.input.Input;
import com.senla.rakickaya.courseplanner.ui.util.printer.Printer;
import com.senla.rakickaya.view.dataExchange.Request;
import com.senla.rakickaya.view.facade.Facade;

public class StudentAddition implements IAction {

	@Override
	public void execute() {
		Input input = Input.getInstance();
		IFacade facade = Facade.getInstance();
		Printer.show("Input student's name : ");
		String name = input.getString();
		Map<TagsRequest,String> map = new HashMap<>();
		map.put(TagsRequest.studentName, name);
		IRequest request = new Request(map);
		IResponse response = facade.addStudent(request);
		Printer.show(response.getObject(TagsResponse.message).toString());
	}

}
