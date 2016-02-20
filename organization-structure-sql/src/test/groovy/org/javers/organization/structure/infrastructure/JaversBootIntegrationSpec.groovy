package org.javers.organization.structure.infrastructure

import org.javers.core.Javers
import org.javers.organization.structure.SqlApplication
import org.javers.organization.structure.domain.Employee
import org.javers.organization.structure.domain.EmployeeRepository
import org.javers.organization.structure.domain.Person
import org.javers.organization.structure.domain.PersonRepository
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = SqlApplication.class)
@WebIntegrationTest(randomPort = true)
class JaversBootIntegrationSpec extends Specification {

    @Autowired
    PersonRepository personRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    Javers javers

    def "should save Employee audit using @SpringDataAuditableAspect"() {

        when:
        def  person =  new Person("mad", "kaz")
        personRepository.save(person)
        employeeRepository.save(new Employee(domainName: "kaz", person: person))

        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId("kaz", Employee).build())

        then:
        snapshots.size() == 1
        //snapshots[0].commitMetadata.author == "?"
    }
}
