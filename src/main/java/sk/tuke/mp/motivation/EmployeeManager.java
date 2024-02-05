package sk.tuke.mp.motivation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeManager implements PersistenceManager<Employee> {
    private final Connection connection;
    private final DepartmentManager departmentManager;

    public EmployeeManager(Connection connection) {
        this.connection = connection;
        this.departmentManager = new DepartmentManager(connection);
    }

    @Override
    public Optional<Employee> get(int id) {

        Employee employee = new Employee();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Employee WHERE ID = ?");
            preparedStatement.setInt(1,id);
            ResultSet response = preparedStatement.executeQuery();
            if(response.next()){
                employee.setId(response.getInt("id"));
                employee.setName(response.getString("name"));
                employee.setSurname(response.getString("surname"));
                employee.setSalary(response.getInt("salary"));

                Optional<Department> result = departmentManager.get(response.getInt("department"));

                employee.setDepartment( result.get());
                return Optional.of(employee);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Employee> getAll() {

        List<Employee> employees = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Employee");
            ResultSet response = preparedStatement.executeQuery();
            while (response.next()){
                Employee employee = new Employee();

                employee.setId(response.getInt("id"));
                employee.setName(response.getString("name"));
                employee.setSurname(response.getString("surname"));
                employee.setSalary(response.getInt("salary"));

                Optional<Department> result = departmentManager.get(response.getInt("department"));

                employee.setDepartment( result.get());

                employees.add(employee);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employees;

    }

    @Override
    public int save(Employee obj) {

        try {
            if(obj.getDepartment().getId()==0){
                departmentManager.save(obj.getDepartment());
            }

            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO Employee(name,surname,salary,department) VALUES(?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1,obj.getName());
            preparedStatement.setString(2,obj.getSurname());
            preparedStatement.setInt(3,obj.getSalary());
            preparedStatement.setInt(4,obj.getDepartment().getId());
            preparedStatement.executeUpdate();

            ResultSet tableKeys = preparedStatement.getGeneratedKeys();
            tableKeys.next();

            obj.setId(tableKeys.getInt(1));
            return tableKeys.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
