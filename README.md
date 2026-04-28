# An app to book Doctor Appointment

>[Watch demonstration](https://youtu.be/P68QQJzL9QM)

## The app features:

### Guest User
- user can visit the app without login (certain features like doctor list, fees will be hidden)

### Authentication (Firebase)
- User can log in/sign in via **Gmail**
- Each **Gmail address** can be used by only **one user** (no duplicate accounts).

#### Doctor Registration (Separate Flow)
- Doctors can register separately.
- Required fields during doctor registration along with their personal details:
    - Upload a picture of themselves
    - Upload license documents

### Database
- Firebase is used to store **User** & **Doctors** information.
- Doctor list is loaded from **Remote Firebase Database**
- User appoinment list is loaded from *local database* **(SQLite)**

### Appointment Booking
- A user need to **sign in/log in** to **book** an apppointment.
- User need to choose a **date** & **time** along with their **medical condition** along with their details for booking.
- User can their past and future appointments in *booking* page.

#### Appointment Booking Flow
- **Doctor Selection:** User must select a doctor first before booking an appointment.
- **Calendar View (Doctor-Specific):** Displays current month's calendar of 7 days including current day.
    - Only dates on which the selected doctor has appointments will be visible. Example: If a doctor has appointments on the 3rd, 4th, and 5th → only those dates will appear or be selectable.
    - Dates with no appointments from that doctor will be grayed out.
- **Appointment Time Selection:** 
    - Each day has its own set of predefined appointment slots
    - Clicking on a highlighted (available) day will show available appointment times for that specific day.
- **Availability Handling:** If no appointments are available on a selected day → display an appropriate message (e.g., "No slots available for this day").
  
