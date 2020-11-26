insert into user (id, user_name, password, active, roles)
values (1, 'foo', 'foo', 'true', 'USER'),
       (2, 'bar', 'bar', 'true', 'USER'),
       (3, 'root', 'root', 'true', 'USER'),
       (4, 'kz', 'kz', 'true', 'USER'),
       (5, 'st', 'st', 'true', 'USER');

insert into client(id, user_name, name, surname, street, city, zip, email, password, code, phone, role)
values (1, 'kz', 'Krzysztof', 'Zabolotny', 'Konows Gate 1E', 'Oslo', '0192', 'kz',
        'kz', '+47', '82010437', 'Client'),
       (2, 'st', 'Stefan', 'Serafin', 'Birken Gate 1E', 'Tromso', '0992', 'st',
        'st', '+47', '92010437', 'Client');

insert into transport (id, departure_date, destination, driver_id, number_of_parcels, order_number)
values ( 1, '2020-01-01', 'Oslo' , 'krzysztof@gmail.com', '0', '0' ),
       ( 2, '2020-02-02', 'Tromso' , 'stefan@gmail.com', '0', '0' ),
       ( 3, '2020-03-02', 'Kristiansand' , 'stefan@gmail.com', '0', '0' ),
       ( 4, '2020-01-04', 'Tromso' , 'stefan@gmail.com', '0', '0' ),
       ( 5, '2020-11-08', 'Bodo' , 'krzysztof@gmail.com', '0' , '0'),
       ( 6, '2020-05-14', 'Tromso' , 'stefan@gmail.com', '0' , '0'),
       ( 7, '2020-06-06', 'Ystad' , 'krzysztof@gmail.com', '0' , '0'),
       ( 8, '2020-07-11', 'Bergen' , 'krzysztof@gmail.com', '0' , '0');