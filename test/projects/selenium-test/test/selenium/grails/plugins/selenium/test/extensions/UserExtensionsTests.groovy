package grails.plugins.selenium.test.extensions

import com.thoughtworks.selenium.HttpCommandProcessor
import grails.plugins.selenium.SeleniumTest
import grails.plugins.selenium.test.auth.Role
import grails.plugins.selenium.test.auth.User
import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

@Mixin(SeleniumTest)
class UserExtensionsTests {

	def authenticateService

	@Before
	void setUpUser() {
		User.withTransaction {
			def user = new User(username: "blackbeard", userRealName: "Edward Teach", enabled: true, email: "blackbeard@energizedwork.com")
			user.passwd = authenticateService.encodePassword("password")
			user.addToAuthorities Role.findByAuthority("ROLE_USER")
			user.save(failOnError: true)
		}
	}

	@After
	void tearDownUser() {
		User.withTransaction {
			User.list().each {User user ->
				user.authorities.each {Role role ->
					user.removeFromAuthorities role
				}
				user.delete()
			}
		}
	}

	@Test
	void canUseSeleniumUserExtensions() {
		selenium.open "/"
		assertThat "login state", selenium.getText("css=.loginInfo"), equalTo("Not logged in")

//		selenium.login "blackbeard", "password"
		selenium.open "/login"
		HttpCommandProcessor proc = selenium.commandProcessor
		proc.doCommand("login", ["blackbeard", "password"] as String[])

		assertThat "login state", selenium.getText("css=.loginInfo"), equalTo("Logged in as blackbeard")
	}

}
