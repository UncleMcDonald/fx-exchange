package com.fx.api.web;

import com.fx.api.feed.AcceptingState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AcceptingState accepting;

    public AdminController(AcceptingState accepting) {
        this.accepting = accepting;
    }

    @GetMapping("/accepting")
    public AcceptingResponse getAccepting() {
        return new AcceptingResponse(accepting.isAccepting());
    }

    @PostMapping("/accepting")
    public AcceptingResponse setAccepting(@RequestBody AcceptingRequest request) {
        return new AcceptingResponse(accepting.set(request.accepting()));
    }

    public record AcceptingRequest(boolean accepting) {}
    public record AcceptingResponse(boolean accepting) {}
}
