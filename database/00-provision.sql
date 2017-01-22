
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
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (code, notebook_code)
);

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (code, notebook_code)
);

-- Expenses
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    category_id INTEGER NOT NULL NOT NULL REFERENCES categories(id),
    value REAL NOT NULL,
    date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    updated_on TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(20) NOT NULL REFERENCES users(login_name),
    notes TEXT
);

CREATE TABLE budgets (
    id SERIAL PRIMARY KEY,
    notebook_code VARCHAR(20) NOT NULL REFERENCES notebooks(code),
    value REAL NOT NULL,
    start_on TIMESTAMP NOT NULL,
    end_on TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(20) NOT NULL REFERENCES users(login_name),
    description TEXT
);

CREATE TABLE budget_expenses (
    budget_id INTEGER NOT NULL REFERENCES budgets(id),
    expense_id BIGINT NOT NULL REFERENCES expenses(id),
    PRIMARY KEY (budget_id, expense_id)
);

CREATE TABLE expense_tags (
    expense_id BIGINT NOT NULL REFERENCES expenses(id),
    tag_id INTEGER NOT NULL REFERENCES tags(id),
    PRIMARY KEY (expense_id, tag_id)
);

CREATE TABLE expense_tags (
    expense_id BIGINT NOT NULL REFERENCES expenses(id),
    tag_id INTEGER NOT NULL REFERENCES tags(id),
    PRIMARY KEY (expense_id, tag_id)
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
    tag_id INTEGER NOT NULL REFERENCES tags(id),
    PRIMARY KEY (page_id, tag_id)
);
