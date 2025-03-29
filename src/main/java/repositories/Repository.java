package repositories;

import utils.DatabaseSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Repository {

    protected Repository() throws SQLException {
        Connection connection;

        connection = openConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS public.universities" +
                    "(" +
                    "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                    "    name character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    city character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    CONSTRAINT universities_pkey PRIMARY KEY (id)" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS public.departments" +
                    "(" +
                    "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                    "    university_id integer NOT NULL," +
                    "    name character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    CONSTRAINT departments_pkey PRIMARY KEY (id)," +
                    "    CONSTRAINT university_department_fkey FOREIGN KEY (university_id)" +
                    "        REFERENCES public.universities (id) MATCH SIMPLE" +
                    "        ON UPDATE NO ACTION" +
                    "        ON DELETE CASCADE" +
                    "        NOT VALID" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS public.professors" +
                    "(" +
                    "    department_id integer NOT NULL," +
                    "    name character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    phone_number character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    degree character varying COLLATE pg_catalog.\"default\" NOT NULL," +
                    "    birthday date NOT NULL," +
                    "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),\n" +
                    "    CONSTRAINT professors_pkey PRIMARY KEY (id)," +
                    "    CONSTRAINT department_professor_fkey FOREIGN KEY (department_id)" +
                    "        REFERENCES public.departments (id) MATCH SIMPLE" +
                    "        ON UPDATE NO ACTION" +
                    "        ON DELETE CASCADE" +
                    "        NOT VALID" +
                    ")");
        } finally {
            connection.close();
        }
    }

    @SuppressWarnings("java:S112")
    protected Connection openConnection() throws SQLException {
        Connection connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connection = DriverManager.getConnection(DatabaseSettings.URL, DatabaseSettings.USERNAME, DatabaseSettings.PASSWORD);
        return connection;
    }
}
