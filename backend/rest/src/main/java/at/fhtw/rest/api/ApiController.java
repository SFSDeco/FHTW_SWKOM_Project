package at.fhtw.rest.api;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "test")
@CrossOrigin(origins = "*")
public class ApiController {
    @GetMapping
    public List<String> getNames() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Hallo");
        stringList.add("Wie");
        stringList.add("schwimmt");
        stringList.add("man?");

        return stringList;
    }
}
