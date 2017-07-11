
class MailHelper {

	private static final String USER_NAME = "******@***.com";
	private static final String PASS_WORD = "*****";

	public void sendEmail() {
		try {
			// establish connection.
			Session session = Session.getDefaultInstance(System.getProperties(), null);
			Store store = session.getStore("imaps");
			store.connect("imap-mail.outlook.com", 993, USER_NAME, PASS_WORD);

			//get inbox and open it in readonly mode.
			Folder folder = store.getFolder("Inbox");
			folder.open(Folder.READ_ONLY);

			// get messages for a particular date.
			Calendar cal = Calendar.getInstance();
			cal.set(2017, 06, 10);
			Date minDate = new Date(cal.getTimeInMillis());

			ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.EQ, minDate);
			Message messages[] = folder.search(minDateTerm);

			for (int i = 0; i < messages.length; i++) {
				System.out.println(messages[i].getSubject());
				if (messages[i].getSubject().equals("Password Verification")) {
					MimeMultipart mimeMultipart = (MimeMultipart) messages[i].getContent();
					String tempPwd = StringUtils.substringBetween(getTextFromMimeMultipart(mimeMultipart),
							"Temporary Password ", "to continue.");
					System.out.println(tempPwd);
					break;

				}
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	// identify content type and return the content in string.
	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break;
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}

	public static void main(final String[] args) {
		new MailHelper().sendEmail();
	}
}
