# swing-basetablemodel

This class help you out to create table in Swing JTable easily

just put an annotation to your model

	public class Person {
	
		@TableColumn(header="ID", order=1)
		private int id;
		
		@TableColumn(header="NAME", order=2)
		private String name;
		
		@TableColumn(header="ADDR", order=3)
		private String address;
		
		// getter, setter or other field
		
	}

Then you can install it in your Jtable like this

	BaseTableModel<Person> personTableModel = new BaseTableModel<Person>(){};
	JTable table = new JTable(personTableModel);
	
Populate your table by doing like this

	List<Person> listPerson = yourMethodToPopulateListPerson();
	personTableModel.setItem(listPerson);

That's all you need to create a table :)
