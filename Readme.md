# Efficient Competition Grading System - ECGS

This project was developed as part of the Software Engineering Project course at Technical University of Wien. Our team of 5 was able to choose the project ourselves. Having been a gymnast throughout most of my childhood, I noticed the need for an competition grading system.

Over the years, there have been multiple attempts to create such a system. It began with manual grading using pen and paper, followed by entering the scores into an Excel sheet for generating rankings. Later, there were efforts involving a Microsoft Access database. However, these systems had various flaws and issues.

The objective of this project is to design a competition system capable of managing participants, grading them, and automatically generating rankings.

Given that gymnastics competitions can have different grading schemas, the administrator should be able to create dynamic grading schemes where judges can assign scores.

For instance, a grading scheme might look like this: `Result` = `A-Note` + `B-Note` - `Deductions`. More complex grading formulas can also be created by the administrator.

## Learning Outcomes of this Course
- Apply the agile methodology Scrum in a practical manner with all its aspects.
- Work with modern frameworks for web application development (Spring Boot, Angular).
- Utilize a distributed version control system (Git).
- Work with modern software development environments (IntelliJ IDEA).
- Understand and apply modern software architectures.
- Implement best practices within a given context.
- Successfully manage group dynamics and social aspects.
- Present project results in a target group-oriented manner.

## Project Description

The project aims to develop a solution that handles the process of data input, evaluation, and transfer of competition data as described in the initial scenario.

To achieve this, a web application is being developed that facilitates the setup, management, and execution of competitions across different sports. Through the system, users acting as Competition Managers can create competitions, add grading stations, and implement scoring systems. Moreover, Competition Managers can choose from predefined scoring systems, create new ones, and create accounts with role-specific permissions for other managerial roles, such as Club Managers and Judges. They can finalize a competition, export or print result lists, and have real-time visibility into the competition's status and progress. After the competition, they can publish the results.

Participants join competitions by participating in grading groups. For some competitions, users can register themselves, while for others, only Club Managers can assign participants. Participants compete within a grading group across one or more stations (e.g., gymnastics: parallel bars, floor, high bar; darts: single discipline). They can access dates for upcoming competitions and view results from past ones.

At each station, an assigned Judge evaluates one or more Participants. Each station belongs to a grading group and has an assigned Judge. Participants are graded by the Judges based on the rules defined in the corresponding station's grading system. Ultimately, individual gradings from all stations are evaluated by the grading system associated with the corresponding grading group.

Club Managers can manage participants and import them using a common file format. They can also view upcoming events for which they have registered.

## Team
- Nathanael Nussbaumer
- Max Auernig
- Martin Reichholf
- Artem Chornyi
- Lorenz Hörburger