package com.senla.rakickaya.courseplanner.ui.actions.students;

import com.senla.rakickaya.courseplanner.api.data_exchange.IResponse;
import com.senla.rakickaya.courseplanner.api.data_exchange.enums.TagsResponse;
import com.senla.rakickaya.courseplanner.api.facade.IFacade;
import com.senla.rakickaya.courseplanner.facade.Facade;
import com.senla.rakickaya.courseplanner.ui.api.actions.IAction;
import com.senla.rakickaya.courseplanner.ui.util.printer.Printer;

public class TotalCountSt implements IAction {

	@Override
	public void execute() {
		IFacade facade = Facade.getInstance();
		IResponse response = facade.getTotalCountStudents();
		int result = (int) response.getObject(TagsResponse.TOTAL_COUNT);
		Printer.show("Total count students: " + result);

	}

}
