# CHU Internship 2024

## 📌 Project Overview
This project was assigned during my internship.  
The goal was to **create a web application to centralize the entire equipment request process**, from the initial request to the delivery invoice, including validation by the relevant authorities.  

> ⚠️ Make sure pop-ups are allowed, otherwise the site will not function properly.

---

## 🛠️ Libraries Used
- **JWT** – for secure login and page access only for authenticated users  
- **iText** – for PDF generation  
- **Gson** – for JSON serialization/deserialization  
- **MySQL Connector** – for database connectivity  

---

## 🔗 Database Connection
- Database connection is configured in `MySQLDatabase.java` and `SomethingDatabase.java`.  
- To create DAOs, models should match the tables in the database.  
- A `script.sql` file is included if you want to modify the database itself.  

---

## ⚙️ Adding/Modifying Methods
- To create a new API on the backend, define the route in `App.java`, which calls a **Controller**, which in turn calls the appropriate **DAO**.  
- On the frontend, the API is called from a service corresponding to the HTML page, following the **MVC pattern**.  
- To create an SSE event: use `*.emit()*` in the Controller on a named channel. To subscribe, use `*.connect()*` and `*.subscribe()*` in the JS script of the HTML page.  
- For JWT functionality, create an `application.properties` file (see *line 30 in UtilisateurController*) with:  

```properties
jwt.secret.key="insert_your_secret_key"
```

## 🔐 Password Hashing
To modify the password hashing logic, edit HashUtil.java.

## ✅ Implemented Features
- Login system with password hashing and JWT stored in Local Storage

- Ability to create a request related to a project or not

- Request sorting by month

- Fully functional search area

- Ability to view request status based on user role

- PDF file generation (see Bug section for details)

- Implementation of 7 user groups (permissions)

- Dynamic page display based on permissions

- Action distribution based on permissions

- Full request validation workflow

- Notifications throughout the process

## 🚧 Features to Implement
- Sending emails for notifications (NotifDao contains the method to get email addresses; create sendEmails in NotifController using line 161)

- Use domain letters to form request ID (e.g., "SI")

- "Check stock" functionality

- Requests for telephony equipment

- A new page displaying all ongoing notifications (API method already exists in the code)

## 🐞 Known Bug
- PDF generation via generatePdf and savePdf has a logic issue: the first click does not work; PDFs function only on the second click.

## 📊 Workflow Diagram
[![](https://mermaid.ink/img/pako:eNqNVl1zojAU_SsMO33TTrdqnfFhZxS01orf9mNxHyLEmhaJA6Fup-1_35APCYR29clzcrk359wbwrvpYR-aLXMT4IO3BRExFvYqNOjv7Myw2-OYgzhZP0Vgv1UoRpO3ADLO2KAgaP3w6_Bno1mJSYRfYOtHrVYT_6sH5JNt63L_N3v62rXhDoQ-tAH-k9F9Sr-iOE_euEuCAhQDApMovzRwe8AjSVRIc-t2cGjhXUmFoTvCBG2QBwjCYbYGQ38VHtXPYfQKI8PCIVUQBDAqesEDtPXMGC1AuHRVXzc2Vye61JYuZXkUKR1uVumapXpWGmFL60pXu6qDpRG9nJFaSN5PK0AwJN_4yQO-8VMLEH5uNs2Leu1UP9tuO34pN7PjUj6EHin306JmE4CCci9tt4938D8mpCOBPFg2Shmdn6CUF0LBut5ceycKHbH9iAzKRsdSpL40Sa3R6anQnV_JC7tD8FBUpXCZJEYKPfVL0Ng0TtQzY3rSx5WtzaWYAr9IlRS4pZCh0HkNA_AK5l6E9qQgpLiQqclWjrNIm3RxoqQ7Jun8OVZ2eS8V5emHVFCeehR6MlaRw04cPS0RCIweojs9IEKPWBITvDPmW7CHPM5x2_v9-TOVIXJwXY6U02h6F6fIoeWG4A0npFXy4jToG0S5OtpGtVo1rjnoMNDnwGLghgObgQEHXQZuOegxMFRaV6jNrqS0qPY2uWaPtjnoM9Dh4IYBi4MBA-IqvGWgy8GQgV5JbWmkQbBe11HrOmpdR63rqHUdta7zZd3jS4LWlXvgQSP2jMPBWAUTFUwl-Da1cpp53pmad67mXah5lyV5-UugzKcZN0A4MOdGCacW3ENh4pL7ZpWkV0ePFikeX5tP1p0YQD6B92I2-XA-iHnkA_koaiiljs0Wp5Xef8p0__pYxjD-kAZ3dMrSKVunujrVy1PazaJvaKQnGevURKemX5RKu5f6-nXJmfLkKGulpMZZPyU1yZoqqan68XBCR-Vzal8lp3ZXcmqPJUc7bVbMHYx2APn0u_g9jVmZZAt3cGW26F8fRC8rcxV-0jiQEDx_Cz2ztQFBDCtmhJOn7REle59-e9kI0Ftkd2ShjwiOHP7dzT6_K-YehL8xljGf_wDbznUc?type=png)](https://mermaid.live/edit#pako:eNqNVl1zojAU_SsMO33TTrdqnfFhZxS01orf9mNxHyLEmhaJA6Fup-1_35APCYR29clzcrk359wbwrvpYR-aLXMT4IO3BRExFvYqNOjv7Myw2-OYgzhZP0Vgv1UoRpO3ADLO2KAgaP3w6_Bno1mJSYRfYOtHrVYT_6sH5JNt63L_N3v62rXhDoQ-tAH-k9F9Sr-iOE_euEuCAhQDApMovzRwe8AjSVRIc-t2cGjhXUmFoTvCBG2QBwjCYbYGQ38VHtXPYfQKI8PCIVUQBDAqesEDtPXMGC1AuHRVXzc2Vye61JYuZXkUKR1uVumapXpWGmFL60pXu6qDpRG9nJFaSN5PK0AwJN_4yQO-8VMLEH5uNs2Leu1UP9tuO34pN7PjUj6EHin306JmE4CCci9tt4938D8mpCOBPFg2Shmdn6CUF0LBut5ceycKHbH9iAzKRsdSpL40Sa3R6anQnV_JC7tD8FBUpXCZJEYKPfVL0Ng0TtQzY3rSx5WtzaWYAr9IlRS4pZCh0HkNA_AK5l6E9qQgpLiQqclWjrNIm3RxoqQ7Jun8OVZ2eS8V5emHVFCeehR6MlaRw04cPS0RCIweojs9IEKPWBITvDPmW7CHPM5x2_v9-TOVIXJwXY6U02h6F6fIoeWG4A0npFXy4jToG0S5OtpGtVo1rjnoMNDnwGLghgObgQEHXQZuOegxMFRaV6jNrqS0qPY2uWaPtjnoM9Dh4IYBi4MBA-IqvGWgy8GQgV5JbWmkQbBe11HrOmpdR63rqHUdta7zZd3jS4LWlXvgQSP2jMPBWAUTFUwl-Da1cpp53pmad67mXah5lyV5-UugzKcZN0A4MOdGCacW3ENh4pL7ZpWkV0ePFikeX5tP1p0YQD6B92I2-XA-iHnkA_koaiiljs0Wp5Xef8p0__pYxjD-kAZ3dMrSKVunujrVy1PazaJvaKQnGevURKemX5RKu5f6-nXJmfLkKGulpMZZPyU1yZoqqan68XBCR-Vzal8lp3ZXcmqPJUc7bVbMHYx2APn0u_g9jVmZZAt3cGW26F8fRC8rcxV-0jiQEDx_Cz2ztQFBDCtmhJOn7REle59-e9kI0Ftkd2ShjwiOHP7dzT6_K-YehL8xljGf_wDbznUc)

## 🔮 Future Plans
To improve the project, a migration to a framework is planned to make the application more robust.

Among possible choices, Node.js and Spring were considered.
Ultimately, Spring was chosen to allow a smoother and faster migration while retaining Java.

✅ Note: This is a prototype. No secret keys or sensitive information are exposed.
