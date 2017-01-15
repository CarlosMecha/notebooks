
-- Schema creation

CREATE TABLE users (
    login_name VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE notebooks (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(20) NOT NULL REFERENCES users(login_name)
);

CREATE TABLE categories (
    code VARCHAR(20),
    notebook_code VARCHAR(20) REFERENCES notebooks(code),
    name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (code, notebook_code)
);

CREATE TABLE tags (
    code VARCHAR(20),
    notebook_code VARCHAR(20) REFERENCES notebooks(code),
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (code, notebook_code)
);

-- Expenses
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    category_code VARCHAR(20) NOT NULL REFERENCES categories(code),
    value REAL NOT NULL,
    date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    updated_on TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(20) NOT NULL REFERENCES users(login_name),
    notes TEXT
);

CREATE TABLE expense_tags (
    expense_id BIGINT NOT NULL REFERENCES expenses(id),
    tag_code VARCHAR(20) NOT NULL REFERENCES tags(code),
    PRIMARY KEY (expense_id, tag_code)
);

-- Pages
CREATE TABLE pages (
    id SERIAL PRIMARY KEY,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    updated_on TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(20) NOT NULL REFERENCES users(login_name)
);

CREATE TABLE page_comments (
    id SERIAL PRIMARY KEY,
    page_id INT NOT NULL REFERENCES pages(id),
    content TEXT NOT NULL,
    wrote_on TIMESTAMP NOT NULL DEFAULT now(),
    wrote_by VARCHAR(20) NOT NULL REFERENCES users(login_name),
    previous_comment_id INT REFERENCES page_comments(id),
    next_comment_id INT REFERENCES page_comments(id)
);

CREATE TABLE page_tags (
    page_id BIGINT NOT NULL REFERENCES pages(id),
    tag_code VARCHAR(20) NOT NULL REFERENCES tags(code),
    PRIMARY KEY (page_id, tag_code)
);
