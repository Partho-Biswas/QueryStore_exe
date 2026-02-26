package com.querystore.server.controller;

import com.querystore.server.model.Query;
import com.querystore.server.model.QueryRequest;
import com.querystore.server.model.User;
import com.querystore.server.repository.QueryRepository;
import com.querystore.server.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/queries")
public class QueryController {
    private final QueryRepository queryRepository;
    private final UserRepository userRepository;

    public QueryController(QueryRepository queryRepository, UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<QueryResponse>> getQueries(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    List<QueryResponse> responses = queryRepository.findByUser(user).stream()
                            .map(q -> new QueryResponse(q.getId().toString(), q.getTitle(), q.getContent()))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(responses);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveQuery(@RequestBody QueryRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    // Check if query with same title already exists for this user
                    List<Query> existingQueries = queryRepository.findByUser(user);
                    Optional<Query> existingQuery = existingQueries.stream()
                            .filter(q -> q.getTitle().equals(request.getTitle()))
                            .findFirst();

                    Query query;
                    if (existingQuery.isPresent()) {
                        query = existingQuery.get();
                        query.setContent(request.getContent());
                    } else {
                        query = new Query();
                        query.setUser(user);
                        query.setTitle(request.getTitle());
                        query.setContent(request.getContent());
                    }
                    
                    queryRepository.save(query);
                    return ResponseEntity.ok("{\"message\":\"Query saved\"}");
                })
                .orElse(ResponseEntity.badRequest().body("{\"error\":\"User not found\"}"));
    }

    @DeleteMapping("/{queryId}")
    public ResponseEntity<?> deleteQuery(@PathVariable String queryId) {
        try {
            queryRepository.deleteById(java.util.UUID.fromString(queryId));
            return ResponseEntity.ok("Query deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting query");
        }
    }

    // Helper class for JSON response
    public static class QueryResponse {
        public String id;
        public String title;
        public String content;

        public QueryResponse(String id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }
    }
}
