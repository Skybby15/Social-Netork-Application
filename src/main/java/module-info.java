module com.projectmap2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdk.compiler;

    opens com.projectmap2.Domain to javafx.base;
    opens com.projectmap2 to javafx.fxml;
    opens com.projectmap2.DTOs to javafx.base;
    exports com.projectmap2;
    exports com.projectmap2.Controllers;
    exports com.projectmap2.UserInterface;
    opens com.projectmap2.Controllers to javafx.fxml;
}