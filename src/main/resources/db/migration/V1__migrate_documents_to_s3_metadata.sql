-- Migrates legacy document metadata from file_type/file_path to the S3-oriented
-- fields used by org.group1.projectbackend.entity.Document.

DO $$
DECLARE
    has_file_type boolean;
    has_file_path boolean;
BEGIN
    IF to_regclass('public.documents') IS NULL THEN
        RETURN;
    END IF;

    SELECT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'documents'
          AND column_name = 'file_type'
    ) INTO has_file_type;

    SELECT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'documents'
          AND column_name = 'file_path'
    ) INTO has_file_path;

    ALTER TABLE documents
        ADD COLUMN IF NOT EXISTS content_type varchar(255),
        ADD COLUMN IF NOT EXISTS storage_key varchar(500),
        ADD COLUMN IF NOT EXISTS bucket varchar(255);

    IF has_file_type THEN
        UPDATE documents
        SET content_type = COALESCE(NULLIF(file_type, ''), 'application/octet-stream')
        WHERE content_type IS NULL OR content_type = '';
    ELSE
        UPDATE documents
        SET content_type = 'application/octet-stream'
        WHERE content_type IS NULL OR content_type = '';
    END IF;

    IF has_file_path THEN
        UPDATE documents
        SET storage_key = COALESCE(
                NULLIF(file_path, ''),
                'legacy/documents/' || id || '/' || COALESCE(NULLIF(file_name, ''), 'document')
            )
        WHERE storage_key IS NULL OR storage_key = '';
    ELSE
        UPDATE documents
        SET storage_key = 'legacy/documents/' || id || '/' || COALESCE(NULLIF(file_name, ''), 'document')
        WHERE storage_key IS NULL OR storage_key = '';
    END IF;

    UPDATE documents
    SET bucket = 'project-documents'
    WHERE bucket IS NULL OR bucket = '';

    ALTER TABLE documents
        ALTER COLUMN content_type SET NOT NULL,
        ALTER COLUMN storage_key SET NOT NULL,
        ALTER COLUMN bucket SET NOT NULL;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_documents_storage_key'
          AND conrelid = 'public.documents'::regclass
    ) THEN
        ALTER TABLE documents
            ADD CONSTRAINT uk_documents_storage_key UNIQUE (storage_key);
    END IF;

    ALTER TABLE documents
        DROP COLUMN IF EXISTS file_type,
        DROP COLUMN IF EXISTS file_path;
END $$;
