# swing-basetablemodel

This class help you out to create table in Swing JTable easily

to create a table model, you should have hav a model class

for example you have this model class

	public class Person {
	
		private int id;
		private String name;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
	}

Then you can create a your PersonTableModel like this

	public class PersonTableModel extends BaseTableModel<Person> {
	
		// this is your table headers
		public PersonTableModel() {
			super("ID", "Name");
		}
	
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Person p = getItem(rowIndex);
			switch (columnIndex) {
			case 0:
				return p.getId();
			case 1:
				return p.getName();
			}
			return null;
		}
	
	}

then you can install it in your Jtable like this

	JTable table = new JTable();
	PersonTableModel personTableModel = new PersonTableModel();
	table.setModel(personTableModel);
	
	List<Person> listPerson = youMethodToPopulateListPerson();
	personTableModel.setItem(listPerson);

Everytime you update your model, you can refresh your table just by calling 

	personTableModel.setItem(listPerson);
