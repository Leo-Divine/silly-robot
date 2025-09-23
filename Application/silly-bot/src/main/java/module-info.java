module silly.bot {
    requires javafx.controls;
    requires javafx.fxml;

    opens silly.bot to javafx.fxml;
    exports silly.bot;
}
