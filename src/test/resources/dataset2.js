db = connect( 'mongodb://localhost:27017/unieventos' );

db.cuentas.insertMany([
    {
        _id: 'C1',
        rol: 'CLIENTE',
        estado: 'ACTIVO',
        email: 'pepeperez@email.com',
        password: 'password',
        usuario: {
            cedula: '1213444',
            nombre: 'Pepito Perez',
            telefonos: ['3012223333', '742358282'],
            direccion: 'Calle 12 # 12-12',
        },
        fechaRegistro: ISODate('2024-07-25T21:41:57.849Z'),
        carrito: [],
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'C2',
        rol: 'CLIENTE',
        estado: 'ACTIVO',
        email: 'rosalopez@email.com',
        password: 'password',
        usuario: {
            cedula: '1213445',
            nombre: 'Rosa Lopez',
            telefono: '3128889191',
            direccion: 'Calle ABC # 12-12',
        },
        fechaRegistro: ISODate('2024-08-02T21:41:57.849Z'),
        carrito: {
            fecha: "2024-10-11T00:22:33.818+00:00",
            items: [
                {
                    _id: ObjectId("6706e5f8fb574535ca3c8254"),
                    idEvento: ObjectId("6706e225dcd8cf50a8533d0a"),
                    cantidad: 50,
                    nombreLocalidad: "General"
                }
            ]
        },
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'C3',
        rol: 'CLIENTE',
        estado: 'ACTIVO',
        email: 'cliente3@email.com',
        password: 'password',
        usuario: {
            cedula: '1213445',
            nombre: 'Omar Gonzales',
            telefono: '3128889191',
            direccion: 'Calle ABC # 12-12',
        },
        fechaRegistro: ISODate('2024-08-25T21:41:57.849Z'),
        carrito: {
            fecha: "2024-10-11T00:22:33.818+00:00",
            items: [
                {
                    _id: ObjectId("6706e5f8fb574535ca3c8253"),
                    idEvento: ObjectId("6706e225dcd8cf50a8533d0a"),
                    cantidad: 60,
                    nombreLocalidad: "VIP"
                }
            ]
        },
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },

    {
        _id: 'Admin2',
        rol: 'ADMINISTRADOR',
        estado: 'ACTIVO',
        email: 'admin1@email.com',
        password: 'password',
        usuario: {
            cedula: 'A1111111',
            nombre: 'Admin Uno',
            telefonos: ['3011234567'],
            direccion: 'Dirección Admin 1',
        },
        fechaRegistro: ISODate('2024-10-01T21:41:57.849Z'),
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'Admin2',
        rol: 'ADMINISTRADOR',
        estado: 'ACTIVO',
        email: 'admin2@email.com',
        password: 'password',
        usuario: {
            cedula: 'A2222222',
            nombre: 'Admin Dos',
            telefonos: ['3017654321'],
            direccion: 'Dirección Admin 2',
        },
        fechaRegistro: ISODate('2024-10-01T21:41:57.849Z'),
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'Admin3',
        rol: 'ADMINISTRADOR',
        estado: 'ACTIVO',
        email: 'admin3@email.com',
        password: 'password',
        usuario: {
            cedula: 'A3333333',
            nombre: 'Admin Tres',
            telefonos: ['3019876543'],
            direccion: 'Dirección Admin 3',
        },
        fechaRegistro: ISODate('2024-10-01T21:41:57.849Z'),
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'Admin4',
        rol: 'ADMINISTRADOR',
        estado: 'ACTIVO',
        email: 'admin4@email.com',
        password: 'password',
        usuario: {
            cedula: 'A4444444',
            nombre: 'Admin Cuatro',
            telefonos: ['3012468135'],
            direccion: 'Dirección Admin 4',
        },
        fechaRegistro: ISODate('2024-10-01T21:41:57.849Z'),
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    },
    {
        _id: 'Admin5',
        rol: 'ADMINISTRADOR',
        estado: 'ACTIVO',
        email: 'admin5@email.com',
        password: 'password',
        usuario: {
            cedula: 'A5555555',
            nombre: 'Admin Cinco',
            telefonos: ['3011357924'],
            direccion: 'Dirección Admin 5',
        },
        fechaRegistro: ISODate('2024-10-01T21:41:57.849Z'),
        _class: 'co.edu.uniquindio.unieventos.documentos.Cuenta'
    }
]);

db.eventos.insertMany([
    {
        id: ObjectId("6706e225dcd8cf50a8533d0a"),
        idUsuario: "Admin1",
        nombre: "Concierto de Rock",
        descripcion: "Un concierto al aire libre con las mejores bandas de rock.",
        Ubicacion: {
            latitud: 12222,
            longitud: 12222
        },
            direccion: "Calle Falsa 123, Bogotá",
            ciudad: "Bogotá",

        imagenPortada: "url_de_imagen_portada.jpg",
        ImagenLocalidades: "url_de_imagen_localidades.jpg",
        estado: "ACTIVO",
        tipo: "CONCIERTO",
        fecha: ISODate("2024-12-16T01:00:00.000Z"),
        localidades: [
            {
                _id: "localidad1",
                nombre: "VIP",
                precio: 200000,
                entradasVendidas: 50,
                capacidadMaxima: 100,
                porcentajeVenta: 50
            },
            {
                _id: "localidad2",
                nombre: "General",
                precio: 100000,
                entradasVendidas: 300,
                capacidadMaxima: 500,
                porcentajeVenta: 60
            }
        ]
    },
    {
        id: ObjectId("7707e336def9cf60b9534e0b"),
        idUsuario: "Admin2",
        nombre: "Festival de Jazz",
        descripcion: "Un festival con los mejores músicos de jazz del país.",
        Ubicacion: {
            latitud: 12345,
            longitud: 12345
        },
            direccion: "Carrera 12 #45, Medellín",
            ciudad: "Medellín",

        imagenPortada: "url_de_imagen_portada2.jpg",
        ImagenLocalidades: "url_de_imagen_localidades2.jpg",
        estado: "ACTIVO",
        tipo: "FESTIVAL",
        fecha: ISODate("2024-11-10T03:00:00.000Z"),
        localidades: [
            {
                _id: "localidad1",
                nombre: "VIP",
                precio: 250000,
                entradasVendidas: 80,
                capacidadMaxima: 100,
                porcentajeVenta: 80
            },
            {
                _id: "localidad2",
                nombre: "General",
                precio: 120000,
                entradasVendidas: 350,
                capacidadMaxima: 400,
                porcentajeVenta: 87.5
            }
        ]
    },
    {
        id: ObjectId("8808f447eeg0dg70c0535f1c"),
        idUsuario: "Admin3",
        nombre: "Feria de Artesanía",
        descripcion: "Una feria con artesanos locales mostrando sus trabajos únicos.",
        Ubicacion: {
            latitud: 54321,
            longitud: 54321
        },
            direccion: "Avenida Siempre Viva, Cali",
            ciudad: "Cali",

        imagenPortada: "url_de_imagen_portada3.jpg",
        ImagenLocalidades: "url_de_imagen_localidades3.jpg",
        estado: "ACTIVO",
        tipo: "FERIA",
        fecha: ISODate("2024-10-20T10:00:00.000Z"),
        localidades: [
            {
                _id: "localidad1",
                nombre: "Stand VIP",
                precio: 300000,
                entradasVendidas: 20,
                capacidadMaxima: 50,
                porcentajeVenta: 40
            },
            {
                _id: "localidad2",
                nombre: "General",
                precio: 150000,
                entradasVendidas: 200,
                capacidadMaxima: 250,
                porcentajeVenta: 80
            }
        ]
    },
    {
        id: ObjectId("9909g558ffh1eh80d1636g2d"),
        idUsuario: "Admin4",
        nombre: "Charla Motivacional",
        descripcion: "Una charla para inspirar y motivar a jóvenes emprendedores.",
        Ubicacion: {
            latitud: 98765,
            longitud: 98765
        },
            direccion: "Calle Emprendedor, Barranquilla",
            ciudad: "Barranquilla",

        imagenPortada: "url_de_imagen_portada4.jpg",
        ImagenLocalidades: "url_de_imagen_localidades4.jpg",
        estado: "ACTIVO",
        tipo: "CHARLA",
        fecha: ISODate("2024-12-01T15:00:00.000Z"),
        localidades: [
            {
                _id: "localidad1",
                nombre: "VIP",
                precio: 180000,
                entradasVendidas: 100,
                capacidadMaxima: 120,
                porcentajeVenta: 83.3
            },
            {
                _id: "localidad2",
                nombre: "General",
                precio: 80000,
                entradasVendidas: 200,
                capacidadMaxima: 250,
                porcentajeVenta: 80
            }
        ]
    },
    {
        id: ObjectId("1011h669ggh2fi90e2737h3e"),
        idUsuario: "Admin5",
        nombre: "Obra de Teatro",
        descripcion: "Una obra de teatro clásica en un escenario moderno.",
        Ubicacion: {
            latitud: 11223,
            longitud: 11223
        },
            direccion: "Teatro Central, Cartagena",
            ciudad: "Cartagena",

        imagenPortada: "url_de_imagen_portada5.jpg",
        ImagenLocalidades: "url_de_imagen_localidades5.jpg",
        estado: "ACTIVO",
        tipo: "TEATRO",
        fecha: ISODate("2024-10-30T20:00:00.000Z"),
        localidades: [
            {
                _id: "localidad1",
                nombre: "Palco",
                precio: 150000,
                entradasVendidas: 30,
                capacidadMaxima: 50,
                porcentajeVenta: 60
            },
            {
                _id: "localidad2",
                nombre: "General",
                precio: 60000,
                entradasVendidas: 400,
                capacidadMaxima: 500,
                porcentajeVenta: 80
            }
        ]
    }
]);

db.cupones.insertMany([
    {
        _id: ObjectId("67075c58bec1e70dbb32664f"),
        nombre: "Halloween para todos",
        codigo: "CUPON-6cd020a3-5469-48e7-a893-333f12b30778",
        descuento: 20,
        estado: "DISPONIBLE",
        tipo: "INDIVIDUAL",
        fechaVencimiento: ISODate("2024-10-28T09:34:33.730Z"),
        beneficiarios: [],
        _class: "co.edu.uniquindio.unieventos.documentos.Cupon"
    },
    {
        _id: ObjectId("67075c58bec1e70dbb326650"),
        nombre: "Descuento de Navidad",
        codigo: "CUPON-3df34567-9876-5432-1234-9876543210ab",
        descuento: 15,
        estado: "DISPONIBLE",
        tipo: "INDIVIDUAL",
        fechaVencimiento: ISODate("2024-12-20T09:34:33.730Z"),
        beneficiarios: [],
        _class: "co.edu.uniquindio.unieventos.documentos.Cupon"
    },
    {
        _id: ObjectId("67075c58bec1e70dbb326651"),
        nombre: "Año Nuevo, Nuevas Ofertas",
        codigo: "CUPON-12345678-5432-1234-9876-abcdef123456",
        descuento: 25,
        estado: "DISPONIBLE",
        tipo: "INDIVIDUAL",
        fechaVencimiento: ISODate("2025-01-05T09:34:33.730Z"),
        beneficiarios: [],
        _class: "co.edu.uniquindio.unieventos.documentos.Cupon"
    },
    {
        _id: ObjectId("67075c58bec1e70dbb326652"),
        nombre: "Semana Santa Especial",
        codigo: "CUPON-abcdef12-3456-7890-abcd-ef1234567890",
        descuento: 30,
        estado: "DISPONIBLE",
        tipo: "INDIVIDUAL",
        fechaVencimiento: ISODate("2024-04-15T09:34:33.730Z"),
        beneficiarios: [],
        _class: "co.edu.uniquindio.unieventos.documentos.Cupon"
    },
    {
        _id: ObjectId("67075c58bec1e70dbb326653"),
        nombre: "Verano Caliente",
        codigo: "CUPON-12345678-8765-4321-fedcba987654",
        descuento: 10,
        estado: "DISPONIBLE",
        tipo: "INDIVIDUAL",
        fechaVencimiento: ISODate("2024-07-31T09:34:33.730Z"),
        beneficiarios: [],
        _class: "co.edu.uniquindio.unieventos.documentos.Cupon"
    }
]);


db.ordenes.insertMany([
    {
        _id: ObjectId('66a2c6a55773597d73593fff'),
        detalle: [
            {
                codigoEvento: ObjectId('66a2c476991cff088eb80aaf'),
                nombreLocalidad: 'PLATEA',
                precio: 50000,
                cantidad: 2
            }
        ],
        codigoCliente: ObjectId('66a2a9aaa8620e3c1c5437be'),
        total: 100000,
        fecha: ISODate('2024-07-25T21:41:57.849Z'),
        codigoPasarela: 'CODIGO_PASARELA',
        pago: {
            codigo: '48dc3dd9-bde1-45ae-b23f-27ee7a261f00',
            fecha: ISODate('2024-07-25T21:41:57.849Z'),
            totalPagado: 100000,
            estado: 'APROBADA',
            metodoPago: 'TARJETA DE CRÉDITO'
        },
        _class: 'co.edu.uniquindio.proyecto.modelo.documentos.Orden'


    }
        [
        {
            _id: {
                $oid: "67071e84b593a631b3c8bb30"
            },
            idCliente: "1091884732",
            codigoPasarela: "2027302781-aaadc1a4-e201-4e46-935b-e2451c03bb82",
            pago: {
                _id: "90132243630",
                moneda: "COP",
                tipoPago: "credit_card",
                estado: "approved",
                detalleEstado: "accredited",
                codigoAutorizacion: "301299",
                valorTransaccion: 9600000,
                fecha: {
                    $date: "2024-10-10T04:20:29.000Z"
                }
            },
            total: 9600000,
            fecha: {
                $date: "2024-10-10T00:23:32.308Z"
            },
            items: [
                {
                    idEvento: {
                        $oid: "6706e225dcd8cf50a8533d0a"
                    },
                    precioUnitario: 100000,
                    nombreLocalidad: "General",
                    cantidad: 50
                },
                {
                    idEvento: {
                        $oid: "6706e225dcd8cf50a8533d0a"
                    },
                    precioUnitario: 200000,
                    nombreLocalidad: "VIP",
                    cantidad: 23
                }
            ],
            _class: "co.edu.uniquindio.unieventos.documentos.Orden"
        },
            {
                _id: {
                    $oid: "6708870f00e54c47c2d0fc4d"
                },
                idCliente: "1091884732",
                idCupon: "6708783c2479536fe20ce581",
                total: 3680000,
                fecha: {
                    $date: "2024-10-11T02:01:51.816Z"
                },
                items: [
                    {
                        idEvento: {
                            $oid: "6706e225dcd8cf50a8533d0a"
                        },
                        precioUnitario: 200000,
                        nombreLocalidad: "VIP",
                        cantidad: 23
                    }
                ],
                _class: "co.edu.uniquindio.unieventos.documentos.Orden"
            },
            {
                _id: {
                    $oid: "670887411f64cd06eb80fc2d"
                },
                idCliente: "1091884732",
                idCupon: "6708783c2479536fe20ce581",
                total: 3680000,
                fecha: {
                    $date: "2024-10-11T02:02:41.227Z"
                },
                items: [
                    {
                        idEvento: {
                            $oid: "6706e225dcd8cf50a8533d0a"
                        },
                        precioUnitario: 200000,
                        nombreLocalidad: "VIP",
                        cantidad: 23
                    }
                ],
                _class: "co.edu.uniquindio.unieventos.documentos.Orden"
            },
            {
                _id: {
                    $oid: "670887927b2fa7530eb7dd56"
                },
                idCliente: "1010080936",
                total: 400000,
                fecha: {
                    $date: "2024-10-11T02:04:02.285Z"
                },
                items: [
                    {
                        idEvento: {
                            $oid: "6706e225dcd8cf50a8533d0a"
                        },
                        precioUnitario: 200000,
                        nombreLocalidad: "VIP",
                        cantidad: 2
                    }
                ],
                _class: "co.edu.uniquindio.unieventos.documentos.Orden"
            },
            {
                _id: {
                    $oid: "670887a3c537120c89164141"
                },
                idCliente: "1010080936",
                total: 400000,
                fecha: {
                    $date: "2024-10-11T02:04:18.987Z"
                },
                items: [
                    {
                        idEvento: {
                            $oid: "6706e225dcd8cf50a8533d0a"
                        },
                        precioUnitario: 200000,
                        nombreLocalidad: "VIP",
                        cantidad: 2
                    }
                ],
                _class: "co.edu.uniquindio.unieventos.documentos.Orden"
            }
        ]


    ]

);

