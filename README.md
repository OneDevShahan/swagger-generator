# Full Stack Swagger YAML Generator

## Project Overview

This project is a full-stack application built with **Spring Boot** for the backend and **React** with **Tailwind CSS** for the frontend. The primary purpose of this application is to dynamically generate Swagger YAML documentation based on a given array of objects. Each object contains necessary details such as the **endpoint**, **HTTP method**, **request schema**, and **response schema**.

The backend generates the Swagger YAML from the provided data, while the frontend provides a clean and user-friendly UI to input the data and view the generated Swagger documentation.

Additionally, the frontend includes features like dynamic text size adjustment for better user experience. Users can modify the size of the text inside the input text areas (such as for entering API details) through simple UI controls.

---

## Features

- **Dynamic Swagger YAML Generation**: Generate Swagger YAML documentation based on user input.
- **React Frontend**: Clean and modern UI powered by React and styled with Tailwind CSS.
- **Spring Boot Backend**: Handles the logic for generating the Swagger YAML file based on the provided input data.
- **Input via Text Area**: Users can pass an array of objects containing the endpoint, HTTP method, request schema, and response schema through a simple text area in the UI.
- **Copy to Clipboard**: A button allows users to copy the generated Swagger YAML to the clipboard for easy use.
- **Validation**: Input data is validated to ensure correct structure and avoid errors in Swagger YAML generation.
- **Dynamic Text Size**: Users can dynamically adjust the text size inside input text areas for improved readability.
- **Responsive Design**: The application is fully responsive, ensuring a seamless experience on all screen sizes.

---

## Tech Stack

### Backend:
- **Spring Boot**: For creating REST APIs and handling the business logic.
- **Swagger**: For generating API documentation and exposing it via endpoints.

### Frontend:
- **React**: For building the user interface.
- **Tailwind CSS**: For styling the frontend components.
- **React Icons**: For adding intuitive icons throughout the UI.
- **React State Management**: Used to handle dynamic changes like adjusting text size.

---

## Setup Instructions

### Prerequisites

- **Java 11+** for running the backend.
- **Node.js (v14 or above)** for running the frontend.

### Backend Setup (Spring Boot)

1. Clone the backend repository: [GitHub Repo](https://github.com/OneDevShahan/swagger-generator)
    ```bash
    git clone <your-backend-repo-url>
    cd <backend-directory>
    ```

2. Install the required dependencies:
    ```bash
    mvn clean install
    ```

3. Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```
   The backend will be accessible on http://localhost:8080.

### Frontend Setup (React with Tailwind CSS)

1. Clone the frontend repository: [GitHub Repo](https://github.com/OneDevShahan/swagger-generator-ui)
    ```bash
    git clone <your-frontend-repo-url>
    cd <frontend-directory>
    ```

2. Install the required dependencies:
    ```bash
    npm install
    ```

3. Start the React development server:
    ```bash
    npm start
    ```
   The frontend will be accessible on http://localhost:3000.

### How to Use

1. **Enter Input Data**: In the frontend UI, you'll find a text area where you can input the array of objects. Each object should have the following structure:

    ```json
    [
      {
        "endpoint": "/api/user",
        "httpMethod": "GET",
        "reqSchema": {},
        "resSchema": {
          "type": "object",
          "properties": {
            "id": { "type": "integer" },
            "name": { "type": "string" }
          }
        }
      }
    ]
    ```

    - `endpoint`: The path for the API endpoint.
    - `httpMethod`: The HTTP method (e.g., GET, POST, etc.).
    - `reqSchema`: The schema for the request body (can be an empty object if no body is needed).
    - `resSchema`: The schema for the response body.

2. **Generate YAML**: After entering the data, click the "Generate Swagger YAML" button to generate the Swagger documentation in YAML format.

3. **Copy YAML**: Once the YAML is generated, you can click the "Copy YAML to Clipboard" button to easily copy the content.

4. **Adjust Text Size**: Use the text size control buttons on the UI to make the text inside the input text areas smaller or larger based on your preference.

### Folder Structure

```bash
├── backend
│   ├── src
│   ├── pom.xml
│   └── README.md
├── frontend
│   ├── public
│   ├── src
│   ├── tailwind.config.js
│   ├── package.json
│   └── README.md
└── README.md
```

- **backend/src:** Contains the Spring Boot backend code for handling API requests and generating the Swagger YAML.
- **frontend/src**: Contains the React app, components, and Tailwind CSS configuration.
- **README.md:** This file!


---

### Contributing
We welcome contributions to improve the project. If you want to contribute, please fork the repository and create a pull request. Follow these steps:

1. Fork the repository.
2. Clone your fork locally.
3. Create a new branch (git checkout -b feature/your-feature).
4. Make your changes.
5. Commit your changes (git commit -m 'Add feature').
6. Push to your fork (git push origin feature/your-feature).
7. Open a pull request.

---

### License
This project is licensed under the MIT License - see the LICENSE file for details.

---

### Contact
For any questions or suggestions, feel free to open an issue or contact us via the project's issues page.

---

### Thank you for using the Full Stack Swagger YAML Generator!

---

### Key Sections in the `README.md`:
- **Project Overview:** Describes what the project does, its tech stack, and key features.
- **Tech Stack:** Lists the technologies used for both frontend and backend.
- **Setup Instructions:** Guides the user to set up both the backend and frontend.
- **How to Use:** Explains how to use the Swagger YAML generator.
- **Folder Structure:** Provides an overview of the project's directory structure.
- **Contributing:** Encourages users to contribute and provides instructions.
- **License:** Includes licensing information.
- **Contact:** Provides a contact link for questions or issues.