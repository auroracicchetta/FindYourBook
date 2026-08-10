<p align="center">
  <img src="src/main/resources/icons/findyourbook_icon_256.png" width="160">
</p>

<h1 align="center">FindYourBook</h1>

<p align="center">Developed for the Software Engineering and Web Design course, University of Rome Tor Vergata.</p>

## Description
FindYourBook is a Java-based book-management platform that connects readers and publishing houses. Readers can search the catalogue by title, author or genre, manage their personal library (To Read / Reading / Read), rate books, and track their reading progress. Publishing houses can manage their catalogue, publish new books, and view statistics on how many times their books have been read. The application supports both a graphical interface (JavaFX) and a command-line interface, with email notifications sent on registration, new book publications, reading goals achieved and reading-inactivity reminders.

* Readers can search for books, manage their personal library and reading statuses, and receive email notifications
* Publishing houses can manage their catalogue, publish new books and monitor reading statistics

## Tecnologies
* Java 17
* Maven
* MySQL
* JavaFX
* SendGrid API (notifiche email)

## Architecture
BCE (Boundary-Control-Entity) - **MVC** (Model-View-Controller) pattern with clear separation between:
* `controller/applicativo` — business logic
* `controller/cli` — CLI user interface
* `controller/gui` — GUI user interface
* `view/cli` — CLI boundary view
* `view/gui` — GUI boundary view
* `dao` — data access layer (DB, File, Memory)
* `model` — domain entities
* `bean` — data transfer objects
* `pattern` — GoF patterns (Singleton, Observer, State)

The system supports three persistence modes:
* **DATABASE** — MySQL (full-version)
* **FILE** — JSON (full-version)
* **MEMORY** — in-memory (demo-version)

## Getting started
At startup, the application asks to select the persistence mode:

* Demo → simulated in-memory data
* Database → MySQL persistence
* File → JSON file persistence

Then, the interface must be selected:

* CLI → text-based interface
* GUI → graphical interface

To use the database mode, first run `database/findyourbook.sql` on MySQL to create the schema and seed data. Then create and configure the following file:

`src/main/resources/db.properties`
with the following content:

```properties
CONNECTION_URL=jdbc:mysql://localhost:3306/findyourbookdb
LOGIN_USER=root
LOGIN_PASS=
READER_USER=fyb_reader
READER_PASS=reader123
PUBLISHER_USER=fyb_publisher
PUBLISHER_PASS=publisher123
SENDGRID_API_KEY=TUA_API_KEY
SENDGRID_FROM_EMAIL=findyourbook.support@gmail.com
```

⚠️ The `db.properties` file includes database credentials and a placeholder for the SendGrid API key. Email notifications in action can be seen in the project demo video.

## Demo credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| Reader | `mario` | qualsiasi |
| Publisher | `mondadori` | qualsiasi |

## Database credentials (modalità MySQL)

| Role | Username | Password |
| :--- | :--- | :--- |
| Reader | `mario` | Password123! |
| Publisher | `mondadori` | Password123! |

Other test accounts available, see `database/findyourbook.sql` for the full list.

## Author
Aurora Cicchetta
