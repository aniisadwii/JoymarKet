-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 11, 2025 at 06:05 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `joymarket2`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `idAdmin` varchar(50) NOT NULL,
  `emergencyContact` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`idAdmin`, `emergencyContact`) VALUES
('AD001', '021-112233');

-- --------------------------------------------------------

--
-- Table structure for table `cartitems`
--

CREATE TABLE `cartitems` (
  `idCustomer` varchar(50) NOT NULL,
  `idProduct` varchar(50) NOT NULL,
  `count` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cartitems`
--

INSERT INTO `cartitems` (`idCustomer`, `idProduct`, `count`) VALUES
('CU002', 'PR001', 1),
('CU002', 'PR002', 1),
('CU003', 'PR003', 3);

-- --------------------------------------------------------

--
-- Table structure for table `couriers`
--

CREATE TABLE `couriers` (
  `idCourier` varchar(50) NOT NULL,
  `vehicleType` varchar(50) NOT NULL,
  `vehiclePlate` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `couriers`
--

INSERT INTO `couriers` (`idCourier`, `vehicleType`, `vehiclePlate`) VALUES
('CO001', 'Motor Beat', 'B 4455 JGO'),
('CO002', 'Honda Vario Ngebul', 'B 7721 KAT'),
('CO003', 'Yamaha Mio Sporty', 'B 5580 LDR'),
('CO004', 'Suzuki Satria FU', 'B 9091 CEP');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `idCustomer` varchar(50) NOT NULL,
  `balance` decimal(15,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`idCustomer`, `balance`) VALUES
('CU001', 0.00),
('CU002', 5000.00),
('CU003', 100000.00),
('CU004', 35000.00),
('CU005', 0.00),
('CU006', 0.00),
('CU007', 62500.00);

-- --------------------------------------------------------

--
-- Table structure for table `deliveries`
--

CREATE TABLE `deliveries` (
  `idOrder` varchar(50) NOT NULL,
  `idCourier` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `deliveries`
--

INSERT INTO `deliveries` (`idOrder`, `idCourier`, `status`) VALUES
('OR1764739875237', 'CO001', 'In Progress'),
('OR1764743488026', 'CO001', 'Delivered'),
('OR1764744431019', 'CO003', 'Pending'),
('OR1765005133568', 'CO002', 'Delivered'),
('OR1765008729781', 'CO002', 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `orderdetails`
--

CREATE TABLE `orderdetails` (
  `idOrder` varchar(50) NOT NULL,
  `idProduct` varchar(50) NOT NULL,
  `qty` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orderdetails`
--

INSERT INTO `orderdetails` (`idOrder`, `idProduct`, `qty`) VALUES
('OR1764739875237', 'PR001', 3),
('OR1764739875237', 'PR003', 1),
('OR1764739875237', 'PR005', 2),
('OR1764743488026', 'PR001', 4),
('OR1764744431019', 'PR003', 2),
('OR1765005133568', 'PR001', 5),
('OR1765008729781', 'PR001', 7),
('OR1765008729781', 'PR003', 1),
('OR1765062888299', 'PR003', 2),
('OR1765157712475', 'PR005', 1),
('OR1765185568050', 'PR001', 1),
('OR1765185568050', 'PR002', 1),
('OR1765190310237', 'PR001', 2),
('OR1765190310237', 'PR003', 2),
('OR1765191021935', 'PR003', 2),
('OR1765191021935', 'PR005', 2),
('OR1765204441870', 'PR003', 2);

-- --------------------------------------------------------

--
-- Table structure for table `orderheaders`
--

CREATE TABLE `orderheaders` (
  `idOrder` varchar(50) NOT NULL,
  `idCustomer` varchar(50) NOT NULL,
  `idPromo` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'Pending',
  `orderedAt` datetime DEFAULT current_timestamp(),
  `totalAmount` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orderheaders`
--

INSERT INTO `orderheaders` (`idOrder`, `idCustomer`, `idPromo`, `status`, `orderedAt`, `totalAmount`) VALUES
('OR1764739875237', 'CU002', NULL, 'In Progress', '2025-12-03 12:31:15', 290000.00),
('OR1764743488026', 'CU002', NULL, 'Delivered', '2025-12-03 13:31:28', 60000.00),
('OR1764744431019', 'CU002', 'PRM01', 'In Progress', '2025-12-03 13:47:11', 45000.00),
('OR1765005133568', 'CU004', NULL, 'Delivered', '2025-12-06 14:12:13', 75000.00),
('OR1765008729781', 'CU004', NULL, 'In Progress', '2025-12-06 15:12:09', 130000.00),
('OR1765062888299', 'CU002', 'PRM01', 'Pending', '2025-12-07 06:14:48', 45000.00),
('OR1765157712475', 'CU005', NULL, 'Pending', '2025-12-08 08:35:12', 110000.00),
('OR1765185568050', 'CU007', 'PRM02', 'Pending', '2025-12-08 16:19:28', 257500.00),
('OR1765190310237', 'CU002', 'PRM02', 'Pending', '2025-12-08 17:38:30', 40000.00),
('OR1765191021935', 'CU002', 'PRM02', 'Pending', '2025-12-08 17:50:21', 135000.00),
('OR1765204441870', 'CU002', 'PRM02', 'Pending', '2025-12-08 21:34:01', 25000.00);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `idProduct` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`idProduct`, `name`, `price`, `stock`, `category`) VALUES
('PR001', 'Apple Fuji Fresh', 15000.00, 38, 'Fruit'),
('PR002', 'Wagyu Beef A5 Slice', 500000.00, 12, 'Meat'),
('PR003', 'Broccoli Organic', 25000.00, 20, 'Vegetable'),
('PR004', 'Salmon Fillet Norway', 120000.00, 20, 'Fish'),
('PR005', 'Indomie Goreng 1 Dus', 110000.00, 95, 'Grocery');

-- --------------------------------------------------------

--
-- Table structure for table `promos`
--

CREATE TABLE `promos` (
  `idPromo` varchar(50) NOT NULL,
  `code` varchar(20) NOT NULL,
  `headline` varchar(255) DEFAULT NULL,
  `discountPercentage` decimal(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promos`
--

INSERT INTO `promos` (`idPromo`, `code`, `headline`, `discountPercentage`) VALUES
('PRM01', 'HEMAT10', 'Diskon 10 Persen', 10.00),
('PRM02', 'MERDEKA', 'Diskon Spesial 50 Persen', 50.00);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `idUser` varchar(50) NOT NULL,
  `fullName` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`idUser`, `fullName`, `email`, `password`, `phone`, `address`, `role`) VALUES
('AD001', 'Admin', 'admin@gmail.com', 'admin123', '0812345678', 'Jl. Admin Pusat No. 11', 'Admin'),
('CO001', 'Mas Kurir', 'kurir@gmail.com', 'kurir123', '081122334455', 'Jl. Aspal No. 5', 'Courier'),
('CO002', 'Bang Paket', 'kurir2@gmail.com', 'kurir234', '081233344455', 'Jl. Melayang No. 12', 'Courier'),
('CO003', 'Mas Antar', 'kurir3@gmail.com', 'kurir345', '081244455566', 'Jl. Kenangan No. 7', 'Courier'),
('CO004', 'Pak Kilat', 'kurir4@gmail.com', 'kurir456', '081255566677', 'Jl. Cepat Tiba No. 3', 'Courier'),
('CU001', 'Anisa Customer', 'anisa1@gmail.com', 'anisa123', '08987654321', 'Jl. Kenangan No. 2', 'Customer'),
('CU002', 'Anisa Customer 2', 'anisa2@gmail.com', 'anisa123', '0123456789', 'jl. lalalala 123', 'Customer'),
('CU003', 'Anisa Customer 3', 'anisa3@gmail.com', 'anisa123', '0987654321', 'Jl. Apa Aja', 'Customer'),
('CU004', 'Anisa Customer 4', 'anisa4@gmail.com', 'anisa123', '1234567890', 'Jl. Alam Sutera', 'Customer'),
('CU005', 'Anisa Customer 5', 'anisa5@gmail.com', 'anisa123', '1234567890', 'Jl. Tangerang 1', 'Customer'),
('CU006', 'Anisa Customer 6', 'anisa6@gmail.com', 'anisa123', '1234567890', 'Jl. Bisa Yok Bisa', 'Customer'),
('CU007', 'Cia Customer 6', 'cia6@gmail.com', 'cia123', '1234567890', 'Jl. Pamulang', 'Customer');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`idAdmin`);

--
-- Indexes for table `cartitems`
--
ALTER TABLE `cartitems`
  ADD PRIMARY KEY (`idCustomer`,`idProduct`),
  ADD KEY `idProduct` (`idProduct`);

--
-- Indexes for table `couriers`
--
ALTER TABLE `couriers`
  ADD PRIMARY KEY (`idCourier`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`idCustomer`);

--
-- Indexes for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD PRIMARY KEY (`idOrder`),
  ADD KEY `idCourier` (`idCourier`);

--
-- Indexes for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD PRIMARY KEY (`idOrder`,`idProduct`),
  ADD KEY `idProduct` (`idProduct`);

--
-- Indexes for table `orderheaders`
--
ALTER TABLE `orderheaders`
  ADD PRIMARY KEY (`idOrder`),
  ADD KEY `idCustomer` (`idCustomer`),
  ADD KEY `idPromo` (`idPromo`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`idProduct`);

--
-- Indexes for table `promos`
--
ALTER TABLE `promos`
  ADD PRIMARY KEY (`idPromo`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`idUser`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`idAdmin`) REFERENCES `users` (`idUser`) ON DELETE CASCADE;

--
-- Constraints for table `cartitems`
--
ALTER TABLE `cartitems`
  ADD CONSTRAINT `cartitems_ibfk_1` FOREIGN KEY (`idCustomer`) REFERENCES `customers` (`idCustomer`) ON DELETE CASCADE,
  ADD CONSTRAINT `cartitems_ibfk_2` FOREIGN KEY (`idProduct`) REFERENCES `products` (`idProduct`) ON DELETE CASCADE;

--
-- Constraints for table `couriers`
--
ALTER TABLE `couriers`
  ADD CONSTRAINT `couriers_ibfk_1` FOREIGN KEY (`idCourier`) REFERENCES `users` (`idUser`) ON DELETE CASCADE;

--
-- Constraints for table `customers`
--
ALTER TABLE `customers`
  ADD CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`idCustomer`) REFERENCES `users` (`idUser`) ON DELETE CASCADE;

--
-- Constraints for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD CONSTRAINT `deliveries_ibfk_1` FOREIGN KEY (`idOrder`) REFERENCES `orderheaders` (`idOrder`) ON DELETE CASCADE,
  ADD CONSTRAINT `deliveries_ibfk_2` FOREIGN KEY (`idCourier`) REFERENCES `couriers` (`idCourier`);

--
-- Constraints for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`idOrder`) REFERENCES `orderheaders` (`idOrder`) ON DELETE CASCADE,
  ADD CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`idProduct`) REFERENCES `products` (`idProduct`);

--
-- Constraints for table `orderheaders`
--
ALTER TABLE `orderheaders`
  ADD CONSTRAINT `orderheaders_ibfk_1` FOREIGN KEY (`idCustomer`) REFERENCES `customers` (`idCustomer`) ON DELETE CASCADE,
  ADD CONSTRAINT `orderheaders_ibfk_2` FOREIGN KEY (`idPromo`) REFERENCES `promos` (`idPromo`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
