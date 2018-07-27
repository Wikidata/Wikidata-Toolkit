package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to interpret Wikimedia language codes in terms of official
 * <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">BCP 47</a> language
 * codes. Unforatunately, the two systems don't agree in all cases. This class
 * incorporates several exceptions, where Wikimedia uses non-standard language
 * codes, including but not limited to the documented <a
 * href="http://meta.wikimedia.org/wiki/Special_language_codes">exceptional
 * language codes</a>. When available, the <a href=
 * "http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry"
 * >IANA-registered codes</a> are used, but in some cases new codes are
 * constructed according to the standard rules.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WikimediaLanguageCodes {

	private static Map<String, String> LANGUAGE_CODES = new HashMap<>();
	static {
		LANGUAGE_CODES.put("aa", "aa"); // Afar
		LANGUAGE_CODES.put("ab", "ab"); // Abkhazian
		LANGUAGE_CODES.put("ace", "ace");
		LANGUAGE_CODES.put("abe", "abe");
		LANGUAGE_CODES.put("aeb-arab", "aeb-Arab");
		LANGUAGE_CODES.put("ady", "ady");
		LANGUAGE_CODES.put("af", "af");
		LANGUAGE_CODES.put("ak", "ak");
		LANGUAGE_CODES.put("aln", "aln");
		LANGUAGE_CODES.put("als", "gsw"); // Swiss German (Alsatian/Alemannic)
		LANGUAGE_CODES.put("am", "am");
		LANGUAGE_CODES.put("ami", "ami");
		LANGUAGE_CODES.put("an", "an");
		LANGUAGE_CODES.put("ang", "ang");
		LANGUAGE_CODES.put("anp", "anp");
		LANGUAGE_CODES.put("ar", "ar");
		LANGUAGE_CODES.put("arc", "arc");
		LANGUAGE_CODES.put("arn", "arn");
		LANGUAGE_CODES.put("arq", "arq");
		LANGUAGE_CODES.put("ary", "ary");
		LANGUAGE_CODES.put("arz", "arz");
		LANGUAGE_CODES.put("as", "as");
		LANGUAGE_CODES.put("ast", "ast");
		LANGUAGE_CODES.put("atj", "atj");
		LANGUAGE_CODES.put("av", "av");
		LANGUAGE_CODES.put("avk", "avk");
		LANGUAGE_CODES.put("ay", "ay");
		LANGUAGE_CODES.put("az", "az");
		LANGUAGE_CODES.put("azb", "azb");
		LANGUAGE_CODES.put("ba", "ba");
		LANGUAGE_CODES.put("ban", "ban");
		LANGUAGE_CODES.put("bar", "bar");
		LANGUAGE_CODES.put("bat-smg", "sgs"); // TODO might be redundant
												// (Samogitian)
		LANGUAGE_CODES.put("bbc", "bbc");
		LANGUAGE_CODES.put("bbc-latn", "bbc-Latn"); // Batak Toba, Latin script
		LANGUAGE_CODES.put("bcc", "bcc");
		LANGUAGE_CODES.put("bcl", "bcl");
		LANGUAGE_CODES.put("be", "be");
		LANGUAGE_CODES.put("be-tarask", "be-tarask"); // Belarusian in
														// Taraskievica
														// orthography
		LANGUAGE_CODES.put("be-x-old", "be-tarask"); // TODO might be redundant
		LANGUAGE_CODES.put("bg", "bg");
		LANGUAGE_CODES.put("bh", "bh");
		LANGUAGE_CODES.put("bho", "bho");
		LANGUAGE_CODES.put("bi", "bi");
		LANGUAGE_CODES.put("bjn", "bjn");
		LANGUAGE_CODES.put("bm", "bm");
		LANGUAGE_CODES.put("bn", "bn");
		LANGUAGE_CODES.put("bnn", "bnn");
		LANGUAGE_CODES.put("bo", "bo");
		LANGUAGE_CODES.put("bpy", "bpy");
		LANGUAGE_CODES.put("bqi", "bqi");
		LANGUAGE_CODES.put("br", "br");
		LANGUAGE_CODES.put("brh", "brh");
		LANGUAGE_CODES.put("brx", "brx");
		LANGUAGE_CODES.put("bs", "bs");
		LANGUAGE_CODES.put("bto", "bto");
		LANGUAGE_CODES.put("bug", "bug");
		LANGUAGE_CODES.put("bxr", "bxr");
		LANGUAGE_CODES.put("ca", "ca");
		LANGUAGE_CODES.put("cbk-zam", "cbk-x-zam"); // Chavacano de Zamboanga
		LANGUAGE_CODES.put("cdo", "cdo");
		LANGUAGE_CODES.put("ceb", "ceb");
		LANGUAGE_CODES.put("ce", "ce");
		LANGUAGE_CODES.put("ch", "ch");
		LANGUAGE_CODES.put("chn", "chn");
		LANGUAGE_CODES.put("cho", "cho");
		LANGUAGE_CODES.put("chr", "chr");
		LANGUAGE_CODES.put("chy", "chy");
		LANGUAGE_CODES.put("ckb", "ckb");
		LANGUAGE_CODES.put("cnr", "cnr");
		LANGUAGE_CODES.put("co", "co");
		LANGUAGE_CODES.put("cop", "cop");
		LANGUAGE_CODES.put("cps", "cps");
		LANGUAGE_CODES.put("cr", "cr");
		LANGUAGE_CODES.put("crh", "crh-Latn"); // TODO might be redundant
		LANGUAGE_CODES.put("crh-cyrl", "crh-Cyrl"); // Crimean Tatar/Crimean
													// Turkish; script Cyrillic
		LANGUAGE_CODES.put("crh-latn", "crh-Latn"); // Crimean Tatar/Crimean
													// Turkish; script Latin
		LANGUAGE_CODES.put("csb", "csb");
		LANGUAGE_CODES.put("cs", "cs");
		LANGUAGE_CODES.put("cu", "cu");
		LANGUAGE_CODES.put("cv", "cv");
		LANGUAGE_CODES.put("cy", "cy");
		LANGUAGE_CODES.put("da", "da");
		LANGUAGE_CODES.put("de-at", "de-AT"); // German, Austria
		LANGUAGE_CODES.put("de-ch", "de-CH"); // German, Switzerland
		LANGUAGE_CODES.put("de", "de"); // German
		LANGUAGE_CODES.put("de-formal", "de-x-formal"); // custom private subtag
														// for formal German
		LANGUAGE_CODES.put("din", "din");
		LANGUAGE_CODES.put("diq", "diq");
		LANGUAGE_CODES.put("dsb", "dsb");
		LANGUAGE_CODES.put("dtp", "dtp");
		LANGUAGE_CODES.put("dty", "dty");
		LANGUAGE_CODES.put("dv", "dv");
		LANGUAGE_CODES.put("dz", "dz");
		LANGUAGE_CODES.put("ee", "ee");
		LANGUAGE_CODES.put("egl", "egl");
		LANGUAGE_CODES.put("el", "el");
		LANGUAGE_CODES.put("eml", "eml"); // Emilian-Romagnol; 'eml' is now
											// retired and split into egl
											// (Emilian) and rgn (Romagnol), but
											// eml will remain a valid BCP 47
											// language tag indefinitely (see
											// bugzilla:34217)
		LANGUAGE_CODES.put("en-ca", "en-CA"); // English; Canada
		LANGUAGE_CODES.put("en", "en"); // English
		LANGUAGE_CODES.put("en-gb", "en-GB"); // English; Great Britain
		LANGUAGE_CODES.put("eo", "eo"); // Esperanto
		LANGUAGE_CODES.put("es", "es");
		LANGUAGE_CODES.put("et", "et");
		LANGUAGE_CODES.put("ett", "ett");
		LANGUAGE_CODES.put("eu", "eu");
		LANGUAGE_CODES.put("ext", "ext");
		LANGUAGE_CODES.put("eya", "eya");
		LANGUAGE_CODES.put("fa", "fa");
		LANGUAGE_CODES.put("ff", "ff");
		LANGUAGE_CODES.put("fi", "fi");
		LANGUAGE_CODES.put("fit", "fit"); // Tornedalen Finnish TODO check
		LANGUAGE_CODES.put("fiu-vro", "vro"); // TODO might be redundant
		LANGUAGE_CODES.put("fj", "fj");
		LANGUAGE_CODES.put("fkv", "fkv");
		LANGUAGE_CODES.put("fo", "fo");
		LANGUAGE_CODES.put("fos", "fos");
		LANGUAGE_CODES.put("frc", "frc");
		LANGUAGE_CODES.put("fr", "fr");
		LANGUAGE_CODES.put("fr-ca", "fr-CA");
		LANGUAGE_CODES.put("frm", "frm");
		LANGUAGE_CODES.put("fro", "fro");
		LANGUAGE_CODES.put("frp", "frp");
		LANGUAGE_CODES.put("frr", "frr");
		LANGUAGE_CODES.put("fuf", "fuf");
		LANGUAGE_CODES.put("fur", "fur");
		LANGUAGE_CODES.put("fy", "fy");
		LANGUAGE_CODES.put("ga", "ga");
		LANGUAGE_CODES.put("gag", "gag");
		LANGUAGE_CODES.put("gan", "gan"); // Gan Chinese; TODO which script?
		LANGUAGE_CODES.put("gan-hans", "gan-Hans"); // Gan Chinese; script Han
													// (simplified)
		LANGUAGE_CODES.put("gan-hant", "gan-Hant"); // Gan Chinese; script Han
													// (traditional)
		LANGUAGE_CODES.put("gd", "gd");
		LANGUAGE_CODES.put("gez", "gez");
		LANGUAGE_CODES.put("gl", "gl");
		LANGUAGE_CODES.put("glk", "glk");
		LANGUAGE_CODES.put("gn", "gn");
		LANGUAGE_CODES.put("gom", "gom");
		LANGUAGE_CODES.put("gor", "gor");
		LANGUAGE_CODES.put("got", "got");
		LANGUAGE_CODES.put("grc", "grc");
		LANGUAGE_CODES.put("gsw", "gsw");
		LANGUAGE_CODES.put("gu", "gu");
		LANGUAGE_CODES.put("gv", "gv");
		LANGUAGE_CODES.put("ha", "ha");
		LANGUAGE_CODES.put("hai", "hai");
		LANGUAGE_CODES.put("hak", "hak");
		LANGUAGE_CODES.put("haw", "haw");
		LANGUAGE_CODES.put("he", "he");
		LANGUAGE_CODES.put("hi", "hi");
		LANGUAGE_CODES.put("hif", "hif");
		LANGUAGE_CODES.put("hif-deva", "hif-Deva");
		LANGUAGE_CODES.put("hif-latn", "hif-Latn");
		LANGUAGE_CODES.put("hil", "hil");
		LANGUAGE_CODES.put("ho", "ho");
		LANGUAGE_CODES.put("hr", "hr");
		LANGUAGE_CODES.put("hrx", "hrx");
		LANGUAGE_CODES.put("hsb", "hsb");
		LANGUAGE_CODES.put("ht", "ht");
		LANGUAGE_CODES.put("hu", "hu");
		LANGUAGE_CODES.put("hy", "hy");
		LANGUAGE_CODES.put("hz", "hz");
		LANGUAGE_CODES.put("ia", "ia");
		LANGUAGE_CODES.put("id", "id");
		LANGUAGE_CODES.put("ie", "ie");
		LANGUAGE_CODES.put("ig", "ig");
		LANGUAGE_CODES.put("ii", "ii"); // Sichuan Yi
		LANGUAGE_CODES.put("ike-cans", "ike-Cans"); // Eastern Canadian
													// Inuktitut, Unified
													// Canadian Aboriginal
													// Syllabics script
		LANGUAGE_CODES.put("ike-latn", "ike-Latn"); // Eastern Canadian
													// Inuktitut, Latin script
		LANGUAGE_CODES.put("ik", "ik");
		LANGUAGE_CODES.put("ilo", "ilo");
		LANGUAGE_CODES.put("io", "io");
		LANGUAGE_CODES.put("is", "is");
		LANGUAGE_CODES.put("it", "it");
		LANGUAGE_CODES.put("iu", "iu");
		LANGUAGE_CODES.put("ja", "ja");
		LANGUAGE_CODES.put("jam", "jam");
		LANGUAGE_CODES.put("jbo", "jbo");
		LANGUAGE_CODES.put("jut", "jut");
		LANGUAGE_CODES.put("jv", "jv");
		LANGUAGE_CODES.put("kaa", "kaa");
		LANGUAGE_CODES.put("kab", "kab");
		LANGUAGE_CODES.put("ka", "ka");
		LANGUAGE_CODES.put("kbd", "kbd");
		LANGUAGE_CODES.put("kbp", "kbp");
		LANGUAGE_CODES.put("kea", "kea");
		LANGUAGE_CODES.put("kg", "kg");
		LANGUAGE_CODES.put("ki", "ki");
		LANGUAGE_CODES.put("kj", "kj");
		LANGUAGE_CODES.put("kjh", "kjh");
		LANGUAGE_CODES.put("kiu", "kiu");
		LANGUAGE_CODES.put("kk-arab", "kk-Arab");// Kazakh; script Arabic
		LANGUAGE_CODES.put("kk-cn", "kk-CN"); // Kazakh; PR China
		LANGUAGE_CODES.put("kk-cyrl", "kk-Cyrl"); // Kazakh; script Cyrillic;
													// TODO IANA has kk with
													// Suppress-Script: Cyrl, so
													// it should be the same as
													// kk
		LANGUAGE_CODES.put("kk", "kk"); // Kazakh
		LANGUAGE_CODES.put("kk-kz", "kk-KZ"); // Kazakh; Kazakhstan
		LANGUAGE_CODES.put("kk-latn", "kk-Latn"); // Kazakh; script Latin
		LANGUAGE_CODES.put("kk-tr", "kk-TR"); // Kazakh; Turkey
		LANGUAGE_CODES.put("kl", "kl");
		LANGUAGE_CODES.put("km", "km");
		LANGUAGE_CODES.put("kn", "kn");
		LANGUAGE_CODES.put("koi", "koi");
		LANGUAGE_CODES.put("ko", "ko");
		LANGUAGE_CODES.put("ko-kp", "ko-KP"); // Korean; Democratic People's
												// Republic of Korea
		LANGUAGE_CODES.put("koy", "koy");
		LANGUAGE_CODES.put("kr", "kr");
		LANGUAGE_CODES.put("krc", "krc");
		LANGUAGE_CODES.put("kri", "kri");
		LANGUAGE_CODES.put("krj", "krj");
		LANGUAGE_CODES.put("krl", "krl");
		LANGUAGE_CODES.put("krx", "krx");
		LANGUAGE_CODES.put("ksh", "mis-x-rip"); // Ripuarian (the code "ksh"
												// refers to Koelsch, a subset
												// of Ripuarian)
		LANGUAGE_CODES.put("ks", "ks");
		LANGUAGE_CODES.put("ku-arab", "ku-Arab"); // Kurdish; script Arabic
		LANGUAGE_CODES.put("ku", "ku"); // Kurdish; TODO this is a
										// macrolanguage; anything more
										// specific? TODO all uses seem to be in
										// Latin -- should this be ku-Latn then?
		LANGUAGE_CODES.put("ku-latn", "ku-Latn"); // Kurdish; script Latin
		LANGUAGE_CODES.put("kv", "kv");
		LANGUAGE_CODES.put("kw", "kw");
		LANGUAGE_CODES.put("ky", "ky");
		LANGUAGE_CODES.put("la", "la");
		LANGUAGE_CODES.put("lad", "lad");
		LANGUAGE_CODES.put("lag", "lag");
		LANGUAGE_CODES.put("lbe", "lbe");
		LANGUAGE_CODES.put("lb", "lb");
		LANGUAGE_CODES.put("lez", "lez");
		LANGUAGE_CODES.put("lfn", "lfn");
		LANGUAGE_CODES.put("lg", "lg");
		LANGUAGE_CODES.put("lij", "lij");
		LANGUAGE_CODES.put("li", "li");
		LANGUAGE_CODES.put("liv", "liv");
		LANGUAGE_CODES.put("lkt", "lkt");
		LANGUAGE_CODES.put("lld", "lld");
		LANGUAGE_CODES.put("lmo", "lmo");
		LANGUAGE_CODES.put("ln", "ln");
		LANGUAGE_CODES.put("lo", "lo");
		LANGUAGE_CODES.put("loz", "loz");
		LANGUAGE_CODES.put("lrc", "lrc");
		LANGUAGE_CODES.put("ltg", "ltg");
		LANGUAGE_CODES.put("lt", "lt");
		LANGUAGE_CODES.put("lus", "lus");
		LANGUAGE_CODES.put("lv", "lv");
		LANGUAGE_CODES.put("lzh", "lzh"); // Literary Chinese
		LANGUAGE_CODES.put("lzz", "lzz");
		LANGUAGE_CODES.put("mai", "mai");
		LANGUAGE_CODES.put("map-bms", "jv-x-bms"); // Basa Banyumasan has no
													// code; jv is a superset
													// (Javanese)
		LANGUAGE_CODES.put("mdf", "mdf");
		LANGUAGE_CODES.put("mg", "mg");
		LANGUAGE_CODES.put("mh", "mh");
		LANGUAGE_CODES.put("mhr", "mhr");
		LANGUAGE_CODES.put("mi", "mi");
		LANGUAGE_CODES.put("mis", "mis");
		LANGUAGE_CODES.put("min", "min");
		LANGUAGE_CODES.put("mk", "mk");
		LANGUAGE_CODES.put("ml", "ml");
		LANGUAGE_CODES.put("mn", "mn");
		LANGUAGE_CODES.put("mnc", "mnc");
		LANGUAGE_CODES.put("mo", "mo");
		LANGUAGE_CODES.put("moe", "moe");
		LANGUAGE_CODES.put("mrj", "mrj");
		LANGUAGE_CODES.put("mr", "mr");
		LANGUAGE_CODES.put("ms", "ms");
		LANGUAGE_CODES.put("mt", "mt");
		LANGUAGE_CODES.put("mul", "mul");
		LANGUAGE_CODES.put("mus", "mus");
		LANGUAGE_CODES.put("mwl", "mwl");
		LANGUAGE_CODES.put("my", "my");
		LANGUAGE_CODES.put("myv", "myv");
		LANGUAGE_CODES.put("mzn", "mzn");
		LANGUAGE_CODES.put("nah", "nah");
		LANGUAGE_CODES.put("na", "na");
		LANGUAGE_CODES.put("nan", "nan");
		LANGUAGE_CODES.put("nap", "nap");
		LANGUAGE_CODES.put("nb", "nb");
		LANGUAGE_CODES.put("nds", "nds"); // Low German
		LANGUAGE_CODES.put("nds-nl", "nds-NL"); // Low German, Netherlands; TODO
												// might be redundant (nds might
												// be the same)
		LANGUAGE_CODES.put("ne", "ne");
		LANGUAGE_CODES.put("new", "new");
		LANGUAGE_CODES.put("ng", "ng");
		LANGUAGE_CODES.put("nui", "nui");
		LANGUAGE_CODES.put("nl-informal", "nl-x-informal"); // custom private
															// subtag for
															// informal Dutch
		LANGUAGE_CODES.put("nl", "nl");
		LANGUAGE_CODES.put("nn", "nn");
		LANGUAGE_CODES.put("no", "no"); // TODO possibly this is "nb" (Norwegian
										// Bokmål); but current dumps have
										// different values for "nb" and "no" in
										// some cases
		LANGUAGE_CODES.put("non", "non");
		LANGUAGE_CODES.put("nov", "nov");
		LANGUAGE_CODES.put("niu", "niu");
		LANGUAGE_CODES.put("nr", "nr");
		LANGUAGE_CODES.put("nrm", "fr-x-nrm"); // Norman; no individual code;
												// lumped with French in ISO
												// 639/3
		LANGUAGE_CODES.put("nso", "nso");
		LANGUAGE_CODES.put("nv", "nv");
		LANGUAGE_CODES.put("nxm", "nxm");
		LANGUAGE_CODES.put("ny", "ny");
		LANGUAGE_CODES.put("nys", "nys");
		LANGUAGE_CODES.put("oc", "oc");
		LANGUAGE_CODES.put("olo", "olo");
		LANGUAGE_CODES.put("om", "om");
		LANGUAGE_CODES.put("ood", "ood");
		LANGUAGE_CODES.put("or", "or");
		LANGUAGE_CODES.put("os", "os");
		LANGUAGE_CODES.put("otk", "otk");
		LANGUAGE_CODES.put("pag", "pag");
		LANGUAGE_CODES.put("pam", "pam");
		LANGUAGE_CODES.put("pa", "pa");
		LANGUAGE_CODES.put("pap", "pap");
		LANGUAGE_CODES.put("pcd", "pcd");
		LANGUAGE_CODES.put("pdc", "pdc");
		LANGUAGE_CODES.put("pdt", "pdt");
		LANGUAGE_CODES.put("pfl", "pfl");
		LANGUAGE_CODES.put("pih", "pih");
		LANGUAGE_CODES.put("pi", "pi");
		LANGUAGE_CODES.put("pjt", "pjt");
		LANGUAGE_CODES.put("pl", "pl");
		LANGUAGE_CODES.put("pms", "pms");
		LANGUAGE_CODES.put("pnb", "pnb");
		LANGUAGE_CODES.put("pnt", "pnt");
		LANGUAGE_CODES.put("ppu", "ppu");
		LANGUAGE_CODES.put("prg", "prg");
		LANGUAGE_CODES.put("ps", "ps");
		LANGUAGE_CODES.put("pt-br", "pt-BR"); // Portuguese, Brazil
		LANGUAGE_CODES.put("pt", "pt"); // Portuguese
		LANGUAGE_CODES.put("pwd", "pwd");
		LANGUAGE_CODES.put("pyu", "pyu");
		LANGUAGE_CODES.put("qu", "qu");
		LANGUAGE_CODES.put("quc", "quc");
		LANGUAGE_CODES.put("qug", "qug");
		LANGUAGE_CODES.put("rgn", "rgn");
		LANGUAGE_CODES.put("rif", "rif");
		LANGUAGE_CODES.put("rm", "rm");
		LANGUAGE_CODES.put("rmy", "rmy");
		LANGUAGE_CODES.put("rn", "rn");
		LANGUAGE_CODES.put("roa-rup", "rup"); // TODO might be redundant
		LANGUAGE_CODES.put("roa-tara", "it-x-tara"); // Tarantino; no language
														// code, ISO 639-3 lumps
														// it with Italian
		LANGUAGE_CODES.put("ro", "ro");
		LANGUAGE_CODES.put("ru", "ru");
		LANGUAGE_CODES.put("rue", "rue");
		LANGUAGE_CODES.put("rup", "rup"); // Macedo-Romanian/Aromanian
		LANGUAGE_CODES.put("ruq-latn", "ruq-Latn");
		LANGUAGE_CODES.put("rw", "rw");
		LANGUAGE_CODES.put("rwr", "rwr");
		LANGUAGE_CODES.put("sah", "sah");
		LANGUAGE_CODES.put("sa", "sa");
		LANGUAGE_CODES.put("sat", "sat");
		LANGUAGE_CODES.put("scn", "scn");
		LANGUAGE_CODES.put("sco", "sco");
		LANGUAGE_CODES.put("sc", "sc");
		LANGUAGE_CODES.put("sd", "sd");
		LANGUAGE_CODES.put("sdc", "sdc");
		LANGUAGE_CODES.put("se", "se");
		LANGUAGE_CODES.put("sei", "sei");
		LANGUAGE_CODES.put("sg", "sg");
		LANGUAGE_CODES.put("sgs", "sgs");
		LANGUAGE_CODES.put("sh", "sh"); // Serbo-Croatian; macrolanguage, not modern but a valid BCP 47 tag
		LANGUAGE_CODES.put("shi", "shi");
		LANGUAGE_CODES.put("shi-latn", "shi-Latn");
		LANGUAGE_CODES.put("shy", "shy");
		LANGUAGE_CODES.put("simple", "en-x-simple"); // custom private subtag
														// for simple English
		LANGUAGE_CODES.put("si", "si");
		LANGUAGE_CODES.put("sjd", "sjd");
		LANGUAGE_CODES.put("sje", "sje");
		LANGUAGE_CODES.put("sjm", "sjm");
		LANGUAGE_CODES.put("sju", "sju");
		LANGUAGE_CODES.put("sk", "sk");
		LANGUAGE_CODES.put("sl", "sl");
		LANGUAGE_CODES.put("sli", "sli");
		LANGUAGE_CODES.put("sm", "sm");
		LANGUAGE_CODES.put("sma", "sma");
		LANGUAGE_CODES.put("smj", "smj");
		LANGUAGE_CODES.put("smn", "smn");
		LANGUAGE_CODES.put("sms", "sms");
		LANGUAGE_CODES.put("sn", "sn");
		LANGUAGE_CODES.put("so", "so");
		LANGUAGE_CODES.put("sq", "sq");
		LANGUAGE_CODES.put("sr-ec", "sr-Cyrl"); // Serbian; Cyrillic script
												// (might change if dialect
												// codes are added to IANA)
		LANGUAGE_CODES.put("sr-el", "sr-Latn"); // Serbian; Latin script (might
												// change if dialect codes are
												// added to IANA)
		LANGUAGE_CODES.put("sr", "sr"); // Serbian TODO should probably be
		// sr-Cyrl too?
		LANGUAGE_CODES.put("srn", "srn");
		LANGUAGE_CODES.put("srq", "srq");
		LANGUAGE_CODES.put("ss", "ss");
		LANGUAGE_CODES.put("ssf", "ssf");
		LANGUAGE_CODES.put("stq", "stq");
		LANGUAGE_CODES.put("st", "st");
		LANGUAGE_CODES.put("su", "su");
		LANGUAGE_CODES.put("sv", "sv");
		LANGUAGE_CODES.put("sw", "sw");
		LANGUAGE_CODES.put("szl", "szl");
		LANGUAGE_CODES.put("ta", "ta");
		LANGUAGE_CODES.put("tcy", "tcy");
		LANGUAGE_CODES.put("te", "te");
		LANGUAGE_CODES.put("tet", "tet");
		LANGUAGE_CODES.put("tg", "tg");
		LANGUAGE_CODES.put("tg-latn", "tg-Latn"); // Tajik; script Latin
		LANGUAGE_CODES.put("tg-cyrl", "tg-Cyrl"); // Tajik; script Cyrillic
		LANGUAGE_CODES.put("th", "th");
		LANGUAGE_CODES.put("ti", "ti");
		LANGUAGE_CODES.put("tk", "tk");
		LANGUAGE_CODES.put("tl", "tl");
		LANGUAGE_CODES.put("tn", "tn");
		LANGUAGE_CODES.put("tokipona", "mis-x-tokipona"); // Tokipona, a
															// constructed
															// language without
															// a code
		LANGUAGE_CODES.put("to", "to");
		LANGUAGE_CODES.put("tpi", "tpi");
		LANGUAGE_CODES.put("tr", "tr");
		LANGUAGE_CODES.put("trv", "trv");
		LANGUAGE_CODES.put("ts", "ts");
		LANGUAGE_CODES.put("tt", "tt"); // Tatar
		LANGUAGE_CODES.put("tt-cyrl", "tt-Cyrl"); // Tatar; Cyrillic script
		LANGUAGE_CODES.put("tt-latn", "tt-Latn"); // Tatar; Latin script
		LANGUAGE_CODES.put("tum", "tum");
		LANGUAGE_CODES.put("tw", "tw");
		LANGUAGE_CODES.put("ty", "ty");
		LANGUAGE_CODES.put("tyv", "tyv");
		LANGUAGE_CODES.put("tzl", "tzl");
		LANGUAGE_CODES.put("udm", "udm");
		LANGUAGE_CODES.put("ug", "ug"); // Uyghur
		LANGUAGE_CODES.put("ug-arab", "ug-Arab"); // Uyghur, Arab script
		LANGUAGE_CODES.put("ug-latn", "ug-Latn"); // Uyghur, Latin script
		LANGUAGE_CODES.put("uk", "uk");
		LANGUAGE_CODES.put("und", "und");
		LANGUAGE_CODES.put("umu", "umu");
		LANGUAGE_CODES.put("ur", "ur");
		LANGUAGE_CODES.put("uun", "uun");
		LANGUAGE_CODES.put("uz", "uz");
		LANGUAGE_CODES.put("tru", "tru");
		LANGUAGE_CODES.put("vec", "vec");
		LANGUAGE_CODES.put("vep", "vep");
		LANGUAGE_CODES.put("ve", "ve");
		LANGUAGE_CODES.put("vi", "vi");
		LANGUAGE_CODES.put("vls", "vls");
		LANGUAGE_CODES.put("vmf", "vmf");
		LANGUAGE_CODES.put("vo", "vo");
		LANGUAGE_CODES.put("vot", "vot");
		LANGUAGE_CODES.put("vro", "vro");
		LANGUAGE_CODES.put("war", "war");
		LANGUAGE_CODES.put("wa", "wa");
		LANGUAGE_CODES.put("wo", "wo");
		LANGUAGE_CODES.put("wuu", "wuu");
		LANGUAGE_CODES.put("xal", "xal");
		LANGUAGE_CODES.put("xh", "xh");
		LANGUAGE_CODES.put("xmf", "xmf");
		LANGUAGE_CODES.put("xpu", "xpu");
		LANGUAGE_CODES.put("yap", "yap");
		LANGUAGE_CODES.put("yi", "yi");
		LANGUAGE_CODES.put("yo", "yo");
		LANGUAGE_CODES.put("yue", "yue"); // Cantonese
		LANGUAGE_CODES.put("za", "za");
		LANGUAGE_CODES.put("zea", "zea");
		LANGUAGE_CODES.put("zh-classical", "lzh"); // TODO might be redundant
		LANGUAGE_CODES.put("zh-cn", "zh-CN"); // Chinese, PRC
		LANGUAGE_CODES.put("zh-hans", "zh-Hans"); // Chinese; script Han
													// (simplified)
		LANGUAGE_CODES.put("zh-hant", "zh-Hant"); // Chinese; script Han
													// (traditional)
		LANGUAGE_CODES.put("zh-hk", "zh-HK"); // Chinese, Hong Kong
		LANGUAGE_CODES.put("zh-min-nan", "nan"); // TODO might be redundant
		LANGUAGE_CODES.put("zh-mo", "zh-MO"); // Chinese, Macao
		LANGUAGE_CODES.put("zh-my", "zh-MY"); // Chinese, Malaysia
		LANGUAGE_CODES.put("zh-sg", "zh-SG"); // Chinese, Singapore
		LANGUAGE_CODES.put("zh-tw", "zh-TW"); // Chinese, Taiwan, Province of
												// China
		LANGUAGE_CODES.put("zh-yue", "yue"); // TODO might be redundant
		LANGUAGE_CODES.put("zh", "zh"); // Chinese; TODO zh is a macrolanguage;
										// should this be cmn? Also, is this the
										// same as zh-Hans or zh-Hant?
		LANGUAGE_CODES.put("zu", "zu"); // Zulu
		LANGUAGE_CODES.put("zun", "zun");
		LANGUAGE_CODES.put("zxx", "zxx");
	}
	
	static Map<String, String> DEPRECATED_LANGUAGE_CODES = new HashMap<>();
	static {
		/*
		 * Source:
		 * https://www.mediawiki.org/wiki/Manual:$wgExtraLanguageCodes
		 */
		DEPRECATED_LANGUAGE_CODES.put("bh","bho"); // Bihari language family
		DEPRECATED_LANGUAGE_CODES.put("no","nb"); // Norwegian language family
		DEPRECATED_LANGUAGE_CODES.put("simple","en"); // Simple English
		/*
		 * Source:
		 * https://www.mediawiki.org/wiki/Manual:$wgDummyLanguageCodes
		 * The ones already included above have been omitted, as well as "qqq" and "qqx".
		 */
		DEPRECATED_LANGUAGE_CODES.put("als", "gsw");
		DEPRECATED_LANGUAGE_CODES.put("bat-smg", "sgs");
		DEPRECATED_LANGUAGE_CODES.put("be-x-old", "be-tarask");
		DEPRECATED_LANGUAGE_CODES.put("fiu-vro", "vro");
		DEPRECATED_LANGUAGE_CODES.put("roa-rup", "rup");
		DEPRECATED_LANGUAGE_CODES.put("zh-classical", "lzh");
		DEPRECATED_LANGUAGE_CODES.put("zh-min-nan", "nan");
		DEPRECATED_LANGUAGE_CODES.put("zh-yue", "yue");
	}

	/**
	 * Get a <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">BCP 47</a>
	 * language code for the given Wikimedia language code.
	 * 
	 * @param wikimediaLanguageCode
	 *            the language code as used by Wikimedia
	 * @return the BCP 47 language code
	 * @throws IllegalArgumentException
	 *             if the given Wikimedia language code is not known. In
	 *             particular, the method will not assume that unknown codes
	 *             agree with BCP 47 by default (since they have no reason to do
	 *             this).
	 */
	public static String getLanguageCode(String wikimediaLanguageCode) {
		if (LANGUAGE_CODES.containsKey(wikimediaLanguageCode)) {
			return LANGUAGE_CODES.get(wikimediaLanguageCode);
		} else {
			throw new IllegalArgumentException("Unknown Wikimedia language \""
					+ wikimediaLanguageCode + "\".");
		}

	}
	
	/**
	 * Translate a Wikimedia language code to its preferred value
	 * if this code is deprecated, or return it untouched if the string
	 * is not a known deprecated Wikimedia language code
	 * 
	 * @param wikimediaLanguageCode
	 * 			the language code as used by Wikimedia
	 * @return
	 * 			the preferred language code corresponding to the original language code
	 */
	public static String fixLanguageCodeIfDeprecated(String wikimediaLanguageCode) {
		if (DEPRECATED_LANGUAGE_CODES.containsKey(wikimediaLanguageCode)) {
			return DEPRECATED_LANGUAGE_CODES.get(wikimediaLanguageCode);
		} else {
			return wikimediaLanguageCode;
		}
	}
}
