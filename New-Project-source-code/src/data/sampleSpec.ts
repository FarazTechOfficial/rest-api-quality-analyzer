// ============================================================================
// Sample OpenAPI specification for the Analyze form.
// Test/sample input only (Category C) — analysis results always come from the
// backend rule engine, never from this file.
// ============================================================================

export const SAMPLE_SPEC = `openapi: 3.0.0
info:
  title: Sample API
  version: 1.0
  description: A small example specification for testing the analyzer.
servers:
  - url: https://api.example.com
paths:
  /v1/users:
    get:
      operationId: listUsers
      summary: List users
      responses:
        "200":
          description: Success
    post:
      operationId: createUser
      summary: Create a user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
      responses:
        "201":
          description: Created
  /v1/users/{id}:
    get:
      operationId: getUserById
      summary: Get a single user
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        "200":
          description: Success
        "404":
          description: Not found
    put:
      operationId: updateUser
      summary: Update a user
      responses:
        "200":
          description: Success
    delete:
      operationId: deleteUser
      summary: Delete a user
      responses:
        "204":
          description: Deleted
`;