package pl.tomaszdziurko.jvm_bloggers.mailing

import pl.tomaszdziurko.jvm_bloggers.blog_posts.domain.BlogPost
import pl.tomaszdziurko.jvm_bloggers.blog_posts.domain.BlogPostRepository
import pl.tomaszdziurko.jvm_bloggers.mailing.domain.MailingAddress
import pl.tomaszdziurko.jvm_bloggers.mailing.domain.MailingAddressRepository
import pl.tomaszdziurko.jvm_bloggers.people.domain.Person
import pl.tomaszdziurko.jvm_bloggers.people.domain.PersonRepository
import pl.tomaszdziurko.jvm_bloggers.utils.NowProvider
import spock.lang.Specification
import spock.lang.Subject

class BlogSummaryMailSenderSpec extends Specification {

    BlogPostRepository blogPostRepository = Mock(BlogPostRepository)
    PersonRepository personRepository = Mock(PersonRepository)
    BlogSummaryMailGenerator blogSummaryMailGenerator = Stub(BlogSummaryMailGenerator)
    MailSender mailSender = Mock(MailSender)
    MailingAddressRepository mailingAddressRepository = Mock(MailingAddressRepository)

    @Subject
    BlogSummaryMailSender summaryMailSender = new BlogSummaryMailSender(blogPostRepository, personRepository, blogSummaryMailGenerator, mailSender, mailingAddressRepository, new NowProvider())


    def "Should not send any mail for empty MailingAddress DB table"() {
        given:
            blogPostRepository.findByPublishedDateAfterOrderByPublishedDateAsc(_) >> [Mock(BlogPost)]
            personRepository.findByDateAddedAfter(_) >> [Mock(Person)]
            mailingAddressRepository.findAll() >>  []
        when:
            summaryMailSender.sendSummary(10)
        then:
            0 * mailSender.sendEmail(_, _, _)
    }

    def "Should send two emails for two records in MailingAddress when there are some new blog posts"() {
        given:
            blogPostRepository.findByPublishedDateAfterOrderByPublishedDateAsc(_) >> [Mock(BlogPost)]
            personRepository.findByDateAddedAfter(_) >> []
            mailingAddressRepository.findAll() >>  [new MailingAddress("email@email.com"), new MailingAddress("email2@email2.com")]
        when:
            summaryMailSender.sendSummary(10)
        then:
            2 * mailSender.sendEmail(_, _, _)
    }

    def "Should send two emails for two records in MailingAddress when there are some new blogs added"() {
        given:
            blogPostRepository.findByPublishedDateAfterOrderByPublishedDateAsc(_) >> []
            personRepository.findByDateAddedAfter(_) >> [Mock(Person)]
            mailingAddressRepository.findAll() >>  [new MailingAddress("email@email.com"), new MailingAddress("email2@email2.com")]
        when:
            summaryMailSender.sendSummary(10)
        then:
            2 * mailSender.sendEmail(_, _, _)
    }

    def "Should not send anything when there are no new blogs nor new blog posts"() {
        given:
            blogPostRepository.findByPublishedDateAfterOrderByPublishedDateAsc(_) >> []
            personRepository.findByDateAddedAfter(_) >> []
            mailingAddressRepository.findAll() >>  [new MailingAddress("email@email.com"), new MailingAddress("email2@email2.com")]
        when:
            summaryMailSender.sendSummary(10)
        then:
            0 * mailSender.sendEmail(_, _, _)
    }




}
