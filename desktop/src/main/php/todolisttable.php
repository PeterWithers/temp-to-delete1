<html>
    <body>
        <!-- <b>Current Known Issues and Requests</b> -->
        <table>
            <?php
            if(isset($_GET["changes"]))
                $csvFile = fopen("kinoath-all_changes.csv", "r");
            else
                $csvFile = fopen("kinoath-all_todo.csv", "r");
            while (($line = fgetcsv($csvFile, 1000, ",")) !== false) {
                echo "<tr>";
                foreach ($line as $cell) {
                    echo "<td>" . htmlspecialchars($cell) . "</td>";
                }
                echo "<tr>\n";
            }
            fclose($csvFile);
            php ?>
        </table>
    </body>
</html>
