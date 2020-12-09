insert into user (id, user_name, password, active, roles)
values (1, 'kz', 'kz', 'true', 'USER'),
       (2, 'st', 'st', 'true', 'USER');

insert into client(id, user_name, name, surname, street, city, zip, email, password, code, phone, role)
values (1, 'kz', 'Krzysztof', 'Zabolotny', 'Konows Gate 1E', 'Oslo', '0192', 'kz',
        'kz', '+47', '82010437', 'Client'),
       (2, 'st', 'Stefan', 'Serafin', 'Birken Gate 1E', 'Tromso', '0992', 'st',
        'st', '+47', '92010437', 'Driver');

insert into transport (id, departure_date, destination, driver_id)
values ( 1, '2020-01-01', 'Oslo' , 'krzysztof@gmail.com' ),
       ( 2, '2020-02-02', 'Tromso' , 'stefan@gmail.com'),
       ( 3, '2020-03-02', 'Oslo' , 'stefan@gmail.com'),
       ( 4, '2020-01-04', 'Tromso' , 'stefan@gmail.com'),
       ( 5, '2020-11-08', 'Oslo' , 'krzysztof@gmail.com'),
       ( 6, '2020-05-14', 'Oslo' , 'stefan@gmail.com'),
       ( 7, '2020-06-06', 'Oslo' , 'krzysztof@gmail.com'),
       ( 8, '2020-07-11', 'Oslo' , 'krzysztof@gmail.com');

insert into parcel (id, in_transport_number, user_name, content, weight, width, length, height, additional_comments, status, destination, departure_date)
values  (1, 1, 'kz','Ubrania',10,20,30,40,'Uzywane ubrania','delivered','Oslo','2020-12-31'),
        (2, 1, 'kz','Zabawki',10,20,30,40,'Uzywane zabawki','delivered','Drammen','2020-12-31'),
        (3, 1, 'kz','Jedzenie',10,20,30,40,'Szynki, kielbasy','delivered','Ystad','2020-12-21'),
        (4, 1, 'kz','Ksiazki',10,20,30,40,'Ksiazki dla biblioteki','transit','Stavanger','2020-12-04');
