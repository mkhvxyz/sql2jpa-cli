CREATE DATABASE SAMPLE_DB;
GO
USE SAMPLE_DB;
GO
CREATE TABLE [dbo].[customer_profiles] (
    id INT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    is_active BIT DEFAULT 1,
    loyalty_points INT DEFAULT 0,
    created_at DATETIME2
);
GO
CREATE TABLE [dbo].[product_categories] (
    cat_id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(150) NOT NULL,
    description TEXT
);