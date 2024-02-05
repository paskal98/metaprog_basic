package sk.tuke.mp.motivation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepartmentManager implements PersistenceManager<Department> {
    private final Connection connection;

    public DepartmentManager(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Department> get(int id) {

        Department department = new Department();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM department WHERE ID = ?");
            preparedStatement.setInt(1,id);
            ResultSet response = preparedStatement.executeQuery();
            if (response.next()){
                department.setId(response.getInt("id"));
                department.setName(response.getString("name"));
                department.setCode(response.getString("code"));
                return Optional.of(department);
            }

        } catch (SQLException e) {
            throw new PersistenceException(e);
        }

        return Optional.empty();

    }

    @Override
    public List<Department> getAll() {

        List<Department> departments;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM department")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Stream.Builder<Department> departmentStreamBuilder = Stream.builder();

            while (resultSet.next()) {
                Department department = new Department(
                        resultSet.getString("name"),
                        resultSet.getString("code")
                );
                department.setId(resultSet.getInt("id"));
                departmentStreamBuilder.add(department);
            }

            departments = departmentStreamBuilder.build().collect(Collectors.toList());

        } catch (SQLException e) {
            throw new PersistenceException(e);
        }

        return departments;
    }

    @Override
    public int save(Department obj) {


        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO department(name, code) values (?,?)", Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, obj.getName());
            preparedStatement.setString(2, obj.getCode());
            preparedStatement.executeUpdate();

            ResultSet tableKeys = preparedStatement.getGeneratedKeys();
            tableKeys.next();

            obj.setId(tableKeys.getInt(1));
            return tableKeys.getInt(1);
        }catch (SQLException e){
            throw  new PersistenceException(e);
        }

    }
}
