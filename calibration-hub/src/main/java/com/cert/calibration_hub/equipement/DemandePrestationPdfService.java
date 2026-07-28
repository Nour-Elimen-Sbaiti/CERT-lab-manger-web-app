package com.cert.calibration_hub.equipement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class DemandePrestationPdfService {

    private final DemandePrestationRepository demandePrestationRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String LOGO_PATH = "static/logo_cert.png";

    private static final Font F_LABEL = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    private static final Font F_LABEL_SOULIGNE = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD | Font.UNDERLINE);
    private static final Font F_VALEUR = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font F_SECTION = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static final Font F_TITRE = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font F_PETIT = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
    private static final BaseColor GRIS_CLAIR = new BaseColor(230, 230, 230);

    @Value("${app.demande-prestation.reference}")
    private String reference;

    @Value("${app.demande-prestation.date-version}")
    private String dateVersion;

    @Value("${app.demande-prestation.revision}")
    private String revision;

    @Value("${app.demande-prestation.client}")
    private String clientNom;

    public DemandePrestationPdfService(DemandePrestationRepository demandePrestationRepository) {
        this.demandePrestationRepository = demandePrestationRepository;
    }

    public byte[] genererPdf(Long demandeId) {
        DemandePrestation d = demandePrestationRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande de prestation introuvable, id = " + demandeId));

        Etalonnage etalonnage = d.getEtalonnage();
        Intervention intervention = etalonnage.getIntervention();
        Equipement e = intervention.getEquipement();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4, 30, 30, 20, 20);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            PdfPTable cadre = new PdfPTable(2);
            cadre.setWidthPercentage(100);
            cadre.setWidths(new float[]{1f, 1f});

            ajouterEntete(doc);

            // ---- Client / Date ----
            cadre.addCell(champBoite("Client :", clientNom));
            cadre.addCell(champBoite("Date :", formater(d.getDateDemande())));
            doc.add(cadre);

            // ---- Matériel à étalonner ----
            PdfPTable materielCadre = new PdfPTable(3);
            materielCadre.setWidthPercentage(100);
            materielCadre.addCell(titreEncadre("Matériel à étalonner", 3));

            materielCadre.addCell(champBoite("Désignation :", e.getDesignation()));
            materielCadre.addCell(champBoite("Type :", e.getModele()));
            materielCadre.addCell(champBoite("Constructeur :", e.getMarque()));
            doc.add(materielCadre);

            PdfPTable serieCadre = new PdfPTable(2);
            serieCadre.setWidthPercentage(100);
            serieCadre.addCell(champBoite("N° de série :", e.getNumeroSerie()));
            serieCadre.addCell(champBoite("Id :", e.getCodeIdentifiant()));
            doc.add(serieCadre);

            PdfPTable etatCadre = new PdfPTable(1);
            etatCadre.setWidthPercentage(100);
            etatCadre.addCell(champBoite("Etat à la livraison :", vide(d.getEtatALaLivraison())));
            etatCadre.addCell(champBoite("Accessoires :", "-"));
            doc.add(etatCadre);

            // ---- Prestation demandée ----
            PdfPTable prestationTitre = new PdfPTable(1);
            prestationTitre.setWidthPercentage(100);
            prestationTitre.addCell(titreEncadre("Prestation demandée", 1));
            doc.add(prestationTitre);

            boolean auLabo = "LABORATOIRE".equals(d.getLieu());
            boolean surSite = "SITE".equals(d.getLieu());

            PdfPTable etalonnageLigne = new PdfPTable(new float[]{2f, 2f, 1f, 1f, 1f});
            etalonnageLigne.setWidthPercentage(100);
            etalonnageLigne.addCell(celluleSansBordure("Etalonnage :", F_LABEL_SOULIGNE));
            etalonnageLigne.addCell(celluleSansBordure("Au Laboratoire", F_VALEUR));
            etalonnageLigne.addCell(caseCoche(auLabo));
            etalonnageLigne.addCell(celluleSansBordure("sur site", F_VALEUR));
            etalonnageLigne.addCell(caseCoche(surSite));
            doc.add(etalonnageLigne);

            PdfPTable comparaisonLigne = new PdfPTable(new float[]{4f, 0.6f, 0.4f});
            comparaisonLigne.setWidthPercentage(100);
            comparaisonLigne.addCell(celluleSansBordure("Comparaison inter laboratoires :", F_LABEL_SOULIGNE));
            comparaisonLigne.addCell(caseCoche(d.isComparaisonInterLabo()));
            comparaisonLigne.addCell(celluleSansBordure("", F_VALEUR));
            doc.add(comparaisonLigne);

            PdfPTable conformiteLigne = new PdfPTable(new float[]{4f, 0.6f, 0.4f});
            conformiteLigne.setWidthPercentage(100);
            conformiteLigne.addCell(celluleSansBordure("Déclaration de conformité :", F_LABEL_SOULIGNE));
            conformiteLigne.addCell(caseCoche(d.isDeclarationConformite()));
            conformiteLigne.addCell(celluleSansBordure("", F_VALEUR));
            doc.add(conformiteLigne);

            PdfPTable emtLigne = new PdfPTable(new float[]{1f, 1f});
            emtLigne.setWidthPercentage(100);
            emtLigne.addCell(celluleSansBordure("Erreur Maximale Tolérée (EMT) : " + vide(etalonnage.getEmt()), F_VALEUR));
            emtLigne.addCell(celluleSansBordure("Bande de Garde : " + vide(etalonnage.getBandeGarde()), F_VALEUR));
            doc.add(emtLigne);

            Paragraph regleIntro = new Paragraph(
                    "Dans le cas d'une demande de déclaration de conformité, le client doit choisir l'une des "
                            + "règles de décision suivantes à appliquer en commun accord avec le laboratoire :", F_PETIT);
            regleIntro.setSpacingBefore(4f);
            regleIntro.setSpacingAfter(4f);
            doc.add(regleIntro);

            doc.add(ligneRegle("Déclaration binaire pour Simple acceptation",
                    d.getRegleDecision() == RegleDecision.BINAIRE_SIMPLE));
            doc.add(ligneRegle("Déclaration binaire avec bande de garde",
                    d.getRegleDecision() == RegleDecision.BINAIRE_BANDE_GARDE));
            doc.add(ligneRegle("Déclaration Non binaire avec bande de garde",
                    d.getRegleDecision() == RegleDecision.NON_BINAIRE_BANDE_GARDE));

            // ---- Commentaire ----
            PdfPTable commentaireCadre = new PdfPTable(1);
            commentaireCadre.setWidthPercentage(100);
            commentaireCadre.setSpacingBefore(8f);

            StringBuilder commentaireTexte = new StringBuilder();
            commentaireTexte.append("le client doit préciser :\n");
            commentaireTexte.append("1- les points à étalonner :\n");
            commentaireTexte.append(vide(d.getPointsAEtalonner()));

            PdfPCell commentaireHeader = new PdfPCell(new Phrase("Commentaire", F_SECTION));
            commentaireHeader.setPadding(4f);
            commentaireCadre.addCell(commentaireHeader);

            PdfPCell commentaireCorps = new PdfPCell(new Phrase(commentaireTexte.toString(), F_VALEUR));
            commentaireCorps.setMinimumHeight(90f);
            commentaireCorps.setPadding(6f);
            commentaireCadre.addCell(commentaireCorps);
            doc.add(commentaireCadre);

            PdfPTable incertitudeLigne = new PdfPTable(new float[]{3f, 1.3f, 0.5f, 1.3f, 0.5f});
            incertitudeLigne.setWidthPercentage(100);
            incertitudeLigne.addCell(celluleSansBordure("2- Le niveau d'incertitude d'étalonnage :", F_VALEUR));
            incertitudeLigne.addCell(celluleSansBordure("Ue >= 1E-04", F_VALEUR));
            incertitudeLigne.addCell(caseCoche(d.getNiveauIncertitude() == NiveauIncertitude.SUPERIEUR_1E_04));
            incertitudeLigne.addCell(celluleSansBordure("Ue < 1E-04", F_VALEUR));
            incertitudeLigne.addCell(caseCoche(d.getNiveauIncertitude() == NiveauIncertitude.INFERIEUR_1E_04));
            doc.add(incertitudeLigne);

            // ---- Signatures ----
            PdfPTable signatures = new PdfPTable(2);
            signatures.setWidthPercentage(100);
            signatures.setSpacingBefore(15f);
            PdfPCell sigResp = new PdfPCell(new Phrase("Signature Responsable\nBureau Réception et livraison", F_LABEL));
            sigResp.setMinimumHeight(70f);
            sigResp.setPadding(6f);
            signatures.addCell(sigResp);
            PdfPCell sigClient = new PdfPCell(new Phrase("Signature Client", F_LABEL));
            sigClient.setMinimumHeight(70f);
            sigClient.setPadding(6f);
            signatures.addCell(sigClient);
            doc.add(signatures);

            Paragraph note = new Paragraph(
                    "\u00b9Les points et/ou Les spécifications de vérification seront fixés en commun accord avec le client.",
                    F_PETIT);
            note.setSpacingBefore(6f);
            doc.add(note);

            doc.close();
            return baos.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + ex.getMessage(), ex);
        }
    }

    // ================= EN-TÊTE =================
    private void ajouterEntete(com.itextpdf.text.Document doc) throws Exception {
        PdfPTable entete = new PdfPTable(new float[]{1f, 3f, 1f});
        entete.setWidthPercentage(100);

        PdfPCell logoCell;
        try {
            Image logo = Image.getInstance(getClass().getClassLoader().getResource(LOGO_PATH));
            logo.scaleToFit(60, 60);
            PdfPTable logoTable = new PdfPTable(1);
            PdfPCell imgCell = new PdfPCell(logo, false);
            imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imgCell.setBorder(Rectangle.NO_BORDER);
            logoTable.addCell(imgCell);
            logoCell = new PdfPCell(logoTable);
        } catch (Exception ex) {
            logoCell = new PdfPCell(new Phrase("Logo\nCERT", F_PETIT));
        }
        logoCell.setBorder(Rectangle.BOX);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(logoCell);

        PdfPCell titreCell = new PdfPCell(new Phrase("DEMANDE DE PRESTATION DE SERVICE", F_TITRE));
        titreCell.setBorder(Rectangle.BOX);
        titreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titreCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(titreCell);

        PdfPCell refCell = new PdfPCell(new Phrase(reference + "\n" + dateVersion + "\n" + revision, F_PETIT));
        refCell.setBorder(Rectangle.BOX);
        refCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        refCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(refCell);

        entete.setSpacingAfter(0f);
        doc.add(entete);
    }

    // ================= UTILITAIRES =================

    private PdfPCell titreEncadre(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_SECTION));
        c.setColspan(colspan);
        c.setBackgroundColor(GRIS_CLAIR);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(4f);
        return c;
    }

    private PdfPCell champBoite(String label, String valeur) {
        Paragraph p = new Paragraph();
        p.add(new com.itextpdf.text.Chunk(label + " ", F_LABEL));
        p.add(new com.itextpdf.text.Chunk(vide(valeur), F_VALEUR));
        PdfPCell c = new PdfPCell(p);
        c.setPadding(5f);
        c.setMinimumHeight(22f);
        return c;
    }

    private PdfPCell celluleSansBordure(String texte, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(3f);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    private PdfPCell caseCoche(boolean cochee) {
        PdfPCell c = new PdfPCell(new Phrase(cochee ? "X" : "", F_LABEL));
        c.setBorder(Rectangle.BOX);
        c.setFixedHeight(14f);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    private PdfPTable ligneRegle(String texte, boolean cochee) {
        PdfPTable t = new PdfPTable(new float[]{1f, 15f});
        t.setWidthPercentage(100);
        t.addCell(caseCoche(cochee));
        t.addCell(celluleSansBordure(texte, F_VALEUR));
        return t;
    }

    private String formater(java.time.LocalDate date) {
        return date != null ? date.format(FMT) : "-";
    }

    private String vide(String valeur) {
        return (valeur == null || valeur.isBlank()) ? "-" : valeur;
    }
}