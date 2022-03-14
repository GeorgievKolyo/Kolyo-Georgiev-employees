import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        //Read from CSV file and put each line in a list from Employees
        List<Employee> employees = readCsvFile("C:\\Users\\User\\Desktop\\Test1.csv");
        //projectID, employees
        Map<Long, List<Employee>> projectWithEmployees = new HashMap<>();
        for (Employee employee : employees) {
            if (!projectWithEmployees.containsKey(employee.getProjectID())) {
                projectWithEmployees.put(employee.getProjectID(), new ArrayList<>());
            }
            projectWithEmployees.get(employee.getProjectID()).add(employee);
        }
        // projectID, {duration, Employee ID #1, Employee ID 2}
        Map<Long, Long[]> result = new HashMap<>();

        for (Long projectID : projectWithEmployees.keySet()) {
            for (int i = 0; i < projectWithEmployees.get(projectID).size(); i++) {
                Employee employee1 = projectWithEmployees.get(projectID).get(i);
                for (int j = i + 1; j < projectWithEmployees.get(projectID).size(); j++) {
                    Employee employee2 = projectWithEmployees.get(projectID).get(j);
                    Long days;
                    if (employee1.getDateFrom().isBefore(employee2.getDateTo()) || employee1.getDateFrom().equals(employee2.getDateTo())) {
                        days = calculateWorkedTogetherDays(employee1, employee2);
                        if (!result.containsKey(projectID)) {
                            result.put(projectID, new Long[]{0L, 0L, 0L});
                        }
                        if (result.get(projectID)[0] <= days) {
                            result.put(projectID, new Long[]{days, employee1.getEmpID(), employee2.getEmpID()});
                        }
                    }
                }
            }
        }
        for (Long projectID : result.keySet()) {
            System.out.println("Employee ID " + result.get(projectID)[1]
                    + ", Employee ID " + result.get(projectID)[2]
                    + ", Project " + projectID
                    + ", Days " + result.get(projectID)[2]);
        }
    }

    private static Long calculateWorkedTogetherDays(Employee employee1, Employee employee2) {
        LocalDate startDay;
        LocalDate endDay;
        int start = employee1.getDateFrom().compareTo(employee2.getDateFrom());
        if (start >= 0) {
            startDay = employee2.getDateFrom();
        }
        else {
            startDay = employee1.getDateFrom();
        }
        int end = employee1.getDateTo().compareTo(employee2.getDateTo());
        if (end >= 0) {
            endDay = employee2.getDateTo();
        }
        else {
            endDay = employee1.getDateTo();
        }
        return ChronoUnit.DAYS.between(startDay, endDay);
    }

    private static List<Employee> readCsvFile(String file) {
        List<Employee> employeesList = new ArrayList<>();
        Path pathToFile = Paths.get(file);
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] attributes  = line.split(",");
                Employee employee = createEmployee(attributes);
                employeesList.add(employee);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeesList;
    }

    private static Employee createEmployee(String[] attributes) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        Long empID = Long.parseLong(attributes[0]);
        Long projectID = Long.parseLong(attributes[1]);
        LocalDate dateFrom = LocalDate.parse(attributes[2], formatter);
        LocalDate dateTo;
        if (attributes[3].equalsIgnoreCase("null")) {
            dateTo = LocalDate.now();
        }
        else {
            dateTo = LocalDate.parse(attributes[3], formatter);
        }

        return new Employee(empID, projectID, dateFrom, dateTo);
    }
}
