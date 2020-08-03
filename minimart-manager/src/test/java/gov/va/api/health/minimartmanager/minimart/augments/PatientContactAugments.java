package gov.va.api.health.minimartmanager.minimart.augments;

import com.github.javafaker.Faker;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient.Address;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient.Contact;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient.Contact.Phone;
import gov.va.api.health.minimartmanager.minimart.augments.Augmentation.Context;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Randomly assign 1 or 2 contacts When 1 contact, randomly choose between Next of Kin and Emergency
 * Contact Randomly populate an address field (or null) Randomly populate 1 or two phone numbers
 * (home/work).
 *
 * <pre>
 *       {
 *       "relationship": [
 *         {
 *           "coding": [
 *             {
 *               "system": "http://hl7.org/fhir/patient-contact-relationship",
 *               "code": "emergency",
 *               "display": "Emergency"
 *             }
 *           ],
 *           "text": "Emergency Contact"
 *         }
 *       ],
 *       "name": {
 *         "text": "UNK,UNKO"
 *       },
 *       "telecom": [
 *         {
 *           "system": "phone",
 *           "value": "09090001234",
 *           "use": "home"
 *         },
 *         {
 *           "system": "phone",
 *           "value": "09990001234",
 *           "use": "work"
 *         }
 *       ],
 *       "address": {
 *         "line": [
 *           "1501 ROXAS BOULEVARD"
 *         ],
 *         "city": "PASAY CITY, METRO MANILA",
 *         "state": "PHILIPPINES"
 *       }
 *     },
 * </pre>
 */
public class PatientContactAugments {
  static DatamartPatient addContact(Context<DatamartPatient> ctx) {
    var contacts = new ArrayList<Contact>(2);
    contacts.add(RandomContact.forContext(ctx).create());
    if (ctx.random().nextBoolean()) {
      contacts.add(RandomContact.forContext(ctx).create());
    }
    ctx.resource().contact(contacts);
    return ctx.resource();
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartPatient.class)
        .whenMatching(p -> p.contact().isEmpty())
        .transform(PatientContactAugments::addContact)
        .build()
        .rewriteFiles();
  }

  @RequiredArgsConstructor(staticName = "forContext")
  private static class RandomContact {
    private static final List<String> RELATIONSHIPS =
        List.of(
            "CIVIL GUARDIAN",
            "VA GUARDIAN",
            "EMERGENCY CONTACT",
            "SECONDARY EMERGENCY CONTACT",
            "NEXT OF KIN",
            "SECONDARY NEXT OF KIN",
            "SPOUSE EMPLOYER");

    private final Faker faker = new Faker();

    private final Context<DatamartPatient> ctx;

    private Address address() {
      var fakeAddress = faker.address();
      return Address.builder()
          .street1(fakeAddress.streetAddress(false))
          .street2(ctx.random().nextBoolean() ? fakeAddress.secondaryAddress() : null)
          .city(fakeAddress.city())
          .state(fakeAddress.state())
          .postalCode(fakeAddress.zipCode())
          .build();
    }

    public Contact create() {
      return Contact.builder()
          .name(name())
          .address(address())
          .phone(phone())
          .type(relationshipType())
          .build();
    }

    private String name() {
      return faker.name().lastName()
          + ctx.random().nextInt(999)
          + ", "
          + faker.name().firstName()
          + ctx.random().nextInt(999);
    }

    private Phone phone() {
      /* Make sure at least one phone number is present. */
      boolean home = ctx.random().nextBoolean();
      return Phone.builder()
          .phoneNumber(home || ctx.random().nextBoolean() ? phoneNumber() : null)
          .workPhoneNumber(!home || ctx.random().nextBoolean() ? phoneNumber() : null)
          .email(ctx.random().nextBoolean() ? faker.name().username() + "@example.com" : null)
          .build();
    }

    private String phoneNumber() {
      return "555"
          + String.format("%03d", ctx.random().nextInt(1000))
          + String.format("%04d", ctx.random().nextInt(10000));
    }

    private String relationshipType() {
      return RELATIONSHIPS.get(ctx.random().nextInt(RELATIONSHIPS.size()));
    }
  }
}
